stage 'CI'
node {
	checkout scm
	// pull dependencies from npm
	bat 'npm install'
}

stage 'PhantomJS Testing'
node {
	try{
	// test with PhantomJS for "fast" "generic" results
	bat 'npm run test-singl-run -- --browsers PhantomJS'
	echo 'Succeeded!'
	}catch (Exception err) {
         echo "Failed: ${err}"
    }
	// archive karma test results (karma is configured to export junit xml files)
	//finally {
	//step([$class: 'JUnitResultArchiver',
	//	testResults: 'test-results/**/test-results.xml'])
	//}
	stage 'Browser Testing'
	echo 'Printed whether above succeeded or failed.'
	runTests("Chrome")
}

 // if (currentBuild.result == null || currentBuild.result == 'SUCCESS' || currentBuild.result == 'FAILURE') {
//parallel integration testing
//stage 'Browser Testing'

//runTests("Chrome")
//node{ echo "{status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] ${env.BRANCH_NAME} ${env.NODE_NAME}'" }
//}
def runTests(browser) {


		// on windows use:
		bat "npm run test-single-run -- --browsers ${browser}"

		step([$class: 'JUnitResultArchiver',
			testResults: 'test-results/**/test-results.xml'])
	
}

