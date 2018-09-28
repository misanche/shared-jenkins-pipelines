def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing CI for application ${pipelineParameters.appName}: Git server (${pipelineParameters.gitUrl}) and branch (${pipelineParameters.gitBranch})"

      stage("Clone application sources") {

         git branch: pipelineParameters.gitBranch, credentialsId: pipelineParameters.gitCredentials, url: pipelineParameters.gitUrl

      }

      stage("Build the Project") {

         configFileProvider(
            [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
            sh 'mvn -s $MAVEN_SETTINGS clean package -DskipTests=true'
         }

      }

      stage('Test & QA') {
         parallel (
            'Unit testing': {

               configFileProvider(
                  [configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
                   sh 'mvn -s $MAVEN_SETTINGS test -DskipTests=true'
               }

            },
            'Static Analysis': {

               echo "TODO:Sonar"  

            }   
         )
      }    

      def pom = readMavenPom file: "pom.xml"
      def developmentVersion = pom.version

      if (pipelineParameters.gitBranch != 'master') {
    
         stage("Maven deploy") {

            configFileProvider([configFile(fileId: 'maven-settings', variable: 'MAVEN_SETTINGS')]) {
               sh 'mvn -s $MAVEN_SETTINGS clean deploy -DskipTests=true'
            }

         }

      }

	 }

      }

   }

}
