def call(Map pipelineParameters) {

   node("maven") {

      echo "Executing mavenCI with ${pipelineParameters})"

   }

}
