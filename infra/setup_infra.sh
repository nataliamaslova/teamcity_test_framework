cd ..

teamcity_tests_directory=$(pwd)
workdir="teamcity_tests_infrastructure"
teamcity_server_workdir="teamcity_server"
selenoid_workdir="selenoid"
teamcity_server_container_name="teamcity-server-instance"
selenoid_container_name="selenoid"
selenoid_ui_container_name="selenoid-ui"

workdir_names=($teamcity_server_workdir $selenoid_workdir)
container_names=($teamcity_server_container_name  $selenoid_container_name $selenoid_ui_container_name)

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
    jetbrains/teamcity-server

echo "Teamcity Server is running..."

################################
echo "Start selenoid"

cd .. && cd $selenoid_workdir
mkdir config
cp $teamcity_tests_directory/infra/browsers.json config/

docker run -d --name $selenoid_container_name \
    -p 4444:4444 \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -v /$teamcity_tests_directory/$workdir/$selenoid_workdir/config:/etc/selenoid:ro \
    aerokube/selenoid:latest-release

echo "Selenoid is running..."

################################
#echo "Start firefox browser"
#docker pull selenoid/vnc:firefox_89.0

################################
echo "Start selenoid-ui"

docker run -d --name $selenoid_ui_container_name                                 \
            -p 80:8080 aerokube/selenoid-ui:latest-release --selenoid-uri "http://$ip:4444"

echo "Selenoid-UI is running..."

################################
echo "Setup teamcity server"

cd .. && cd ..
mvn clean test -Dtest=SetupFirstStartTest#startUpTest

################################
echo "Parse superuser token"

superuser_token=$(grep -o 'Super user authentication token: [0-9]*' $teamcity_tests_directory/$workdir/$teamcity_server_workdir/logs/teamcity-server.log | awk '{print $NF}')
echo "Super user token: $superuser_token"

################################
echo "Run system tests with config data"

echo "host=$ip:8111
superUserToken=$superuser_token
remote=http://$ip:4444/wd/hub
browser=firefox" > $teamcity_tests_directory/src/main/resources/config.properties
cat $teamcity_tests_directory/src/main/resources/config.properties

echo "Run API/UI tests"
mvn test
