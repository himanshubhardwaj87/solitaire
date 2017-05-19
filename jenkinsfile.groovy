stage 'CI'
node {
    checkout scm
    // pull dependencies from npm
    // on windows use: 
bat 'npm install'
    //sh 'npm install'
    // test with PhantomJS for "fast" "generic" results
    // on windows use: 
	stage 'PhantomJS Testing'
bat 'npm run test-single-run -- --browsers PhantomJS'
    //sh 'npm run test-single-run -- --browsers PhantomJS'
    // archive karma test results (karma is configured to export junit xml files)
    step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
          
}

//parallel integration testing
stage 'Browser Testing'
    runTests("Chrome")
	node{
echo "{status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] ${env.BRANCH_NAME} ${env.NODE_NAME}'"
}
def runTests(browser) {
    node {
        // on windows use: bat 'del /S /Q *'
       // sh 'rm -rf *'

        //unstash 'everything'

        // on windows use: 
		bat "npm run test-single-run -- --browsers ${browser}"
        //sh "npm run test-single-run -- --browsers ${browser}"

        step([$class: 'JUnitResultArchiver', 
              testResults: 'test-results/**/test-results.xml'])
    }
}

