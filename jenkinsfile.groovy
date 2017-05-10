stage 'CI' 
node('master') {
	try{
notify('Started')
    git branch: 'master', 
        url: 'https://github.com/himanshubhardwaj87/solitaire.git'
    bat 'npm install'
    bat 'npm run test-single-run -- --browsers PhantomJS'
    step([$class: 'JUnitResultArchiver', 
          testResults: 'test-results/**/test-results.xml'])
}     
 catch (err) {
        notify("Error: ${err}")
        currentBuild.result = 'FAILURE'
    }  
}
stage 'Browser Testing'
parallel chrome:{
    runTests("Chrome")
}, firfox:{
    runTests("Firefox")
}
node {
    notify("Deploy to staging?")
}

input 'Deploy to staging?'

// limit concurrency so we don't perform simultaneous deploys
// and if multiple pipelines are executing, 
// newest is only that will be allowed through, rest will be canceled
stage name: 'Deploy to staging', concurrency: 1
node {
    // write build number to index page so we can see this update
bat "echo '<h1>${env.BUILD_DISPLAY_NAME}</h1>' >> app/index.html"

    
    // deploy to a docker container mapped to port 3000
 bat 'docker-compose up -d --build'
   // sh 'docker-compose up -d --build'
    
    notify 'Solitaire Deployed!'
}

notify('Execution Complete')
 
def runTests(browser) {
    node {
		try{
     bat "npm run test-single-run -- --browsers ${browser}"

        step([$class: 'JUnitResultArchiver', 
              testResults: 'test-results/**/test-results.xml'])
    }
	 catch (err) {
        notify("Error: ${err}")
        currentBuild.result = 'FAILURE'
    }
	}
}
def notify(status){
    emailext (
      to: "himanshubhardwaj87@gmail.com",
      subject: "${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
      body: """<p>${status}: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
        <p>Check console output at <a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a></p>""",
    )
}