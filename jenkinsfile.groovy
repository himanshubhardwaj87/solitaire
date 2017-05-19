stage 'CI'
node {
    checkout scm
    // pull dependencies from npm
bat 'npm install'
    // test with PhantomJS for "fast" "generic" results         
}

def runTests(browser) {
    node {
		bat "npm run test-single-run -- --browsers ${browser}"
        step([$class: 'JUnitResultArchiver', 
              testResults: 'test-results/**/test-results.xml'])
    }
}

def paralleltask = [:]  
paralleltask["PhantomJS Testing"] = {  
    node() {

bat 'npm run test-single-run -- --browsers PhantomJS'
    // archive karma test results (karma is configured to export junit xml files)
    step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
    }
}

paralleltask["Browser Testing"] = {  
    node() {

		runTests("Chrome")
    }
}

parallel paralleltask