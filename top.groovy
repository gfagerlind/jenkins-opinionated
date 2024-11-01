// top jenkins file, without boilerplate
{ ->
    declareParameter(string(name: 'JOB_TYPE', defaultValue: ''))
    env.JOB_TYPE = params.JOB_TYPE
    if (params.JOB_TYPE == "d") {
        import_file("dep.groovy")
    }
    if (params.JOB_TYPE == 'p') {
        import_file("postmerge.groovy")
    }
    if (params.JOB_TYPE == 'c') {
        import_file("check.groovy")
    }
    // What about not dependent gate? - that is trivial, just remove the dependentGate closure,
    // What if i want to do wacky combinations of stageWithWip and dependentGate? - go right ahead, but i dont support dependentGate in dependentGate because i dont know what it would mean
}
