# Exemple of executing the program

mvn -q clean install

echo -e "Test transit_private, data from file, nOfTests =DEFAULT, threshold=DEFAULT\n"
mvn -q exec:java -Dexec.mainClass="com.smomic.execution.ClasSpdb" -Dexec.args="'-c=transit_private' '-f=src/main/resources/transit_private_T200'"

echo -e "\n\nTest transit_walk, nOfTests = 10, threshold=100\n"
mvn -q exec:java -Dexec.mainClass="com.smomic.execution.ClasSpdb" -Dexec.args="'-c=transit_walk' '-g' '-n=10' '-t=100'"
