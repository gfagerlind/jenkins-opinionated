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

apply_dependent_gate = {
    sh("git rev-parse --verify CHERRY_PICK_HEAD 2> /dev/null && git cherry-pick --abort || true")
    sh("git checkout ${params.GIT_REF}~ -B test")
    try {
        sh("git cherry-pick ${env.gateContext.replace('\n',' ')}")
     } catch (hudson.AbortException e) {
        // NOTE: currently there is no speculation that what ever we have a conflict with
        // will fail and be thrown out of the queue, so this could pass - we just abort here.
        sh("git diff")
        sh("git cherry-pick --abort")
        throw e
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
            checkout_scm()
            // this is ugly, but see it as just a demo
            // TODO: fix so this works in demo mode
            if (env.gateContext && env.gateContext != params.GIT_REF) {
                apply_dependent_gate()
            }
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
