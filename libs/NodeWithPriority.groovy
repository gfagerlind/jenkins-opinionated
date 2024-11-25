{ ->
    nodeWithPriority = { label, priority, amount, cls ->
        queue(name: label, priority: priority, wip: amount) {
            node(label) {
                cls()
            }
        }
    }
}
