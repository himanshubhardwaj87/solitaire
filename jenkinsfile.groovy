stage 'CI'
node {
    checkout scm
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
}
}


paralleltask["Browser Testing"] = {  
    node() {
		runTests("Chrome")
		archive()
    }
}

parallel paralleltask