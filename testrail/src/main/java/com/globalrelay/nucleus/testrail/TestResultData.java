package com.globalrelay.nucleus.testrail;

public class TestResultData {

    private String testrailId;
    private String resultName;
    private String exceptionMsg;
    private String annotatedVersion;
    private String duration;
    private String defects;

    TestResultData merge(TestResultData other) {
        if (other.getExceptionMsg() != null) {
            this.exceptionMsg = mergeMessages(this.exceptionMsg, other.exceptionMsg);
        }

        this.duration = mergeDuration(this.duration, other.duration);
        this.resultName = mergeStatusNames(this.resultName, other.resultName);

        return this;
    }

    public String getTestrailId() {
        return testrailId;
    }

    public void setTestrailId(String testrailId) {
        this.testrailId = testrailId;
    }

    public String getResultName() {
        return resultName;
    }

    public void setResultName(String resultName) {
        this.resultName = resultName;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public String getAnnotatedVersion() {
        return annotatedVersion;
    }

    public void setAnnotatedVersion(String annotatedVersion) {
        this.annotatedVersion = annotatedVersion;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDefects() {
        return defects;
    }

    public void setDefects(String defects) {
        this.defects = defects;
    }

    private String mergeDuration(final String duration1, final String duration2) {
        Double d1 = Double.parseDouble(duration1) * 1000.0;
        Double d2 = Double.parseDouble(duration2) * 1000.0;
        return String.valueOf((d1 + d2) / 1000.0);
    }

    private String mergeMessages(final String msg1, final String msg2) {
        return msg1 == null
                ? msg2
                : String.format("%1$s %n==========%n %2$s", msg1, msg2);
    }

    private String mergeStatusNames(final String res1, final String res2) {
        if (res1.equals(TestStatus.PASSED.name()) && res2.equals(TestStatus.PASSED.name())) {
            return TestStatus.PASSED.name();
        } else {
            return TestStatus.FAILED.name();
        }
    }

    String[] toArray() {
        return new String[]{ testrailId, resultName, exceptionMsg, annotatedVersion, duration, defects };
    }
}
