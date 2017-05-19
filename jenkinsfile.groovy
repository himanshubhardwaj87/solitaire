import hudson.model.*
import groovy.json.*
import java.net.URL
import groovy.transform.Field

stage 'CI'
node {
    checkout scm
	 echo "GIT_COMMIT is ${env.GIT_COMMIT}"
    // pull dependencies from npm
bat 'npm install' 
stash name: 'everything', 
          excludes: 'test-results/**', 
          includes: '**'	
}

def runTests(browser) {
		unstash 'everything'
		bat "npm run test-single-run -- --browsers ${browser}"

}

def archive(){
 step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
    }


def paralleltask = [:]  
paralleltask["PhantomJS Testing"] = {  
    node() {

bat 'npm run test-single-run -- --browsers PhantomJS'
    // archive karma test results (karma is configured to export junit xml files)
   archive()
   echo "[${env.BUILD_NUMBER}] ${GIT_BRANCH}"
   def branch = sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
   echo branch
}
}


paralleltask["Browser Testing"] = {  
    node() {
		runTests("Chrome")
		archive()
		echo "[${env.BUILD_NUMBER}] ${GIT_BRANCH}"
    }
}

parallel paralleltask