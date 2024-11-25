// Milestones has the unfortunate behaviour that they kill other runnings jobs
// instead, you should self die when reaching a milestone that is already claimed by someone newer
// So never abort other jobs, only abort yourself when arriving late.
// Note: This is regardless of what the wip limit is set to,
// one could consider to keep stages alive,so when timeout exists, it will will run older stuff.
// Usually thats not what you want, but the option is there.
@NonCPS
def poorMansMilestone(stage) {
    def build = currentBuild.getNextBuild()
    milestone = "milestone: ${stage}\n"
    while (build) {
        echo "checking ${build.number} for '${milestone}'"
        if (build.buildVariables.milestones?.contains(milestone)){
            error("Milestone ${stage} taken by ${build.number}")
        }
        build = build.getNextBuild()
    }
    echo "Milestone ${stage}"
    env.milestones += milestone
}
{ ->
    // set intial value of the milestones
    env.milestones = ""
    stageWithWip = { args, cls ->
        def name = args.name
        def wip = args.wip ?: 1
        def latest_is_greatest = args.latest_is_greatest == false ? false : true
        // This can be used inside dependentGate if you want
        assert name, "stageWithWip needs a `name` parameter"
        stage(name) {
            queue(name: name, wip: wip, latest_is_greatest: latest_is_greatest){
                if (latest_is_greatest) poorMansMilestone(name)
                cls.call()
            }
        }
    }
}
