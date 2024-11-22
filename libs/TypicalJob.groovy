// thing method gives you the id to link to the sublog
def getLocalUrlId(flowNode = null) {
    if(!flowNode) {
        flowNode = getContext(org.jenkinsci.plugins.workflow.graph.FlowNode)
    }
    while ( flowNode.parents) {
        if(flowNode instanceof org.jenkinsci.plugins.workflow.cps.nodes.StepStartNode) {
            return flowNode.id
        }
        flowNode = flowNode.parents[0]
    }
}

// TODO: yeah, just add an optional node label and timeout or what ever you want.
def job(timeout_s, label, cmd) {
    def id = getLocalUrlId()
    // These URLs can be used to send to notification systems, like some visualisation
    // or messages in the pull request and similar.
    def url = "${env.BUILD_URL}/pipeline-console/?selected-node=${id}"
    def log = "${env.BUILD_URL}/pipeline-console/log?nodeId=${id}"
    try {
        echo "running ${url}"
        node(label) {
            checkout scm
            // this is ugly, but see it as just a demo
            // TODO: fix so this works in demo mode
            // if (env.gateContext) {
            //     sh('git cherry-pick ${env.gateContext.replace("\n"," ")')
            // }
            timeout(time: timeout_s, unit: 'SECONDS') {
                cmd()
            }
        }
        echo "exit ${log}"
    } catch (hudson.AbortException e) {
        // Here you can do clean ups
        echo "${url} failed"
        throw e
    }
}

def _shJob(timeout_s, label, cmd) {
    job(timeout_s, label) {
        sh(cmd)
    }
}

def call() {
    shJob = {timeout_s, label, cmd -> _shJob(timeout_s, label, cmd)}
}

return this
