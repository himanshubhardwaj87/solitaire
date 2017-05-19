stage 'CI'
node {
	checkout scm
	// pull dependencies from npm
	bat 'npm install'
}

stage 'PhantomJS Testing'
	try{
	// test with PhantomJS for "fast" "generic" results
	bat 'npm run test-single-run -- --browsers PhantomJS'
	 currentBuild.result = 'SUCCESS'
	}catch (Exception err) {
        currentBuild.result = 'FAILURE'
    }
	// archive karma test results (karma is configured to export junit xml files)
	finally {
	step([$class: 'JUnitResultArchiver',
		testResults: 'test-results/**/test-results.xml'])
	}
}

  if (currentBuild.result == null) {
//parallel integration testing
stage 'Browser Testing'

runTests("Chrome")
node{ echo "{status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] ${env.BRANCH_NAME} ${env.NODE_NAME}'" }
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

