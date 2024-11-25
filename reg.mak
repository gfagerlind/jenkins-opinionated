TARGETS := a b c d e
SCOPES := check gate postmerge post-post-merge
# You could for sure dynamically decide on the scope depending on
# the changed files if you want
check: a b c

gate: b c d

postmerge: b c d e
post-post-merge: a

# to make it easy to inject errors - not a nice pattern,
# see jenkins_server_bootstrap/Makefile
-include test_*.mak
TIMEOUT=30
$(TARGETS): NODE=label
a b c d: TIMEOUT=60
e: TIMEOUT=60

ifndef OPINIONATED
$(TARGETS):
	@echo starting $@
	@sleep 10
	$(if $(SHOULD_FAIL),echo $@ should fail && false)
	@echo finished $@
else
.NOTPARALLEL:
$(TARGETS):
	@echo '$@: { shJob($(TIMEOUT),"$(NODE)", "./reg.sh $@") },'
$(TARGETS): first
first:
	@echo 'return ['
$(SCOPES):
	@echo ']'
endif

