test_all:
NORMAL_COMMIT:= failing slipthrough ok
COMMIT:= failing slipthrough ok mergeconflict
DEPENDENT_COMMIT:= mergeconflict
failing: SCOPE=gate
slipthrough: SCOPE=postmerge
failing slipthrough: SHOULD_FAIL=true
$(COMMIT): setup

ok: slipthrough
slipthrough: failing
mergeconflict: failing

setup:
	git branch -f test/master HEAD


TMP_DIR:=$(shell mktemp -d)


# Test idea:
# create two parallel fixes
# failing, slipthrough, merge-conflict, ok, and fixup
# failing fails in gate and forces a restart
# slipthrough works in gate, but fails in postmerge - you should have added slipthrough to gate/check
# NOTE: there should probably be a check/gate test to verify that everything in postmerge is part of check/gate
$(NORMAL_COMMIT):
	git worktree add $(TMP_DIR)/$@ --force -B "test/$@" master
	echo 'TARGETS += $@' >> $(TMP_DIR)/$@/test_$@.mak
	echo '$(SCOPE): $@' >> $(TMP_DIR)/$@/test_$@.mak
	$(if $(SHOULD_FAIL),echo "$@: SHOULD_FAIL=true" >> $(TMP_DIR)/$@/test_$@.mak)
	git -C $(TMP_DIR)/$@ add test_$@.mak
	git -C $(TMP_DIR)/$@ commit -m "$@"
	curl -X POST -L --user admin:admin  'localhost:8123/job/proj-a/job/job/buildWithParameters?SCOPE=gate&GIT_REF=origin/test/$@'
	git worktree remove -f $(TMP_DIR)/$@

$(DEPENDENT_COMMIT):
	git worktree add $(TMP_DIR)/$@ --force -B "test/$@" master
	echo 'TARGETS += $@' >> $(TMP_DIR)/$@/test_failing.mak
	echo '$(SCOPE): $@' >> $(TMP_DIR)/$@/test_failing.mak
	git -C $(TMP_DIR)/$@ add test_failing.mak
	git -C $(TMP_DIR)/$@ commit -m "$@"
	curl -X POST -L --user admin:admin  'localhost:8123/job/proj-a/job/job/buildWithParameters?SCOPE=gate&GIT_REF=origin/test/$@'
	git worktree remove -f $(TMP_DIR)/$@

trigger_check trigger_postmerge: setup

trigger_postmerge:
	curl -X POST -L --user admin:admin  'localhost:8123/job/proj-a/job/job/buildWithParameters?SCOPE=postmerge&GIT_REF=origin/test/master'

trigger_check:
	curl -X POST -L --user admin:admin  'localhost:8123/job/proj-a/job/job/buildWithParameters?SCOPE=check&GIT_REF=origin/test/master'

test_all: $(COMMIT) trigger_check trigger_postmerge
