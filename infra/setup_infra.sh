cd ..

teamcity_tests_directory=$(pwd)
workdir="teamcity_tests_infrastructure"
teamcity_server_workdir="teamcity_server"
teamcity_agent_workdir="teamcity_agent"
selenoid_workdir="selenoid"
teamcity_server_container_name="teamcity_server_instance"
teamcity_agent_container_name="teamcity_agent_instance"
selenoid_container_name="selenoid"
selenoid_ui_container_name="selenoid_ui"

workdir_names=($teamcity_server_workdir $teamcity_agent_workdir $selenoid_workdir)
container_names=($teamcity_server_container_name  $teamcity_agent_container_name $selenoid_container_name $selenoid_ui_container_name)

################################
echo "Define IP"
export ip=192.168.0.165
echo "Current IP: $ip"

################################
echo "Delete previous run data"
rm -rf $workdir

################################
echo "Create new dirs"
mkdir $workdir
cd $workdir

for dir in "${workdir_names[@]}"; do
  mkdir $dir
done

################################
echo "Stop and delete containers"
for container in "${container_names[@]}"; do
  docker stop $container
  docker rm $container
done

################################
echo "Start teamcity server"

cd $teamcity_server_workdir

docker run -d --name $teamcity_server_container_name -u 0  \
    -v /$teamcity_tests_directory/$workdir/$teamcity_server_workdir/logs:/opt/teamcity/logs  \
    -p 8111:8111 \
    jetbrains/teamcity-server:2023.11.1

echo "Teamcity Server is running..."

################################
echo "Start Teamcity Agent"

cd ..
cd $teamcity_agent_workdir

docker run -e SERVER_URL=http://$ip:8111 -u 0 -d --name $teamcity_agent_container_name \
          -v /$teamcity_tests_directory/$workdir/$teamcity_agent_workdir/conf:/data/teamcity_agent/conf \
          jetbrains/teamcity-agent:2023.11.1

echo "Teamcity Agent is running..."

################################
echo "Config data"

echo "host=$ip:8111
remote=http://$ip:4444/wd/hub
browser=firefox" > $teamcity_tests_directory/src/main/resources/config.properties
cat $teamcity_tests_directory/src/main/resources/config.properties

################################
echo "Start selenoid"

cd .. && cd $selenoid_workdir
mkdir config
cp $teamcity_tests_directory/infra/browsers.json config/

#    if local browser used - apply only one in docker line / -v /var/run/docker.sock:/var/run/docker.sock \
docker run -d --name $selenoid_container_name \
    -p 4444:4444 \
    -v //var/run/docker.sock:/var/run/docker.sock \
    -v /$teamcity_tests_directory/$workdir/$selenoid_workdir/config:/etc/selenoid:ro \
    aerokube/selenoid:latest-release

echo "Selenoid is running..."

################################
echo "Start firefox browser"
docker pull selenoid/vnc:firefox_89.0

################################
echo "Start selenoid-ui"

docker run -d --name $selenoid_ui_container_name                                 \
            -p 80:8080 aerokube/selenoid-ui:latest-release --selenoid-uri "http://$ip:4444"

echo "Selenoid-UI is running..."

################################
echo "Setup teamcity server"

cd .. && cd ..
mvn clean test -Dtest=SetupFirstStartTest#setupTeamCityServerTest

################################
echo "Parse superuser token"

superuser_token=$(grep -o 'Super user authentication token: [0-9]*' $teamcity_tests_directory/$workdir/$teamcity_server_workdir/logs/teamcity-server.log | awk '{print $NF}')
echo "Super user token: $superuser_token"

################################
echo "Config data"

echo "host=$ip:8111
superUserToken=$superuser_token
remote=http://$ip:4444/wd/hub
browser=firefox" > $teamcity_tests_directory/src/main/resources/config.properties
cat $teamcity_tests_directory/src/main/resources/config.properties

################################
echo "Setup teamcity agent"

mvn test -Dtest=AuthorizeAgentTest#authorizeAgent

echo "Run API/UI tests"
mvn test

################################
echo "Create Swagger report for API tests coverage"
# Doc: https://github.com/viclovsky/swagger-coverage

swagger-coverage-commandline -s http://$ip:8111/app/rest/swagger.json -i swagger-coverage-output
