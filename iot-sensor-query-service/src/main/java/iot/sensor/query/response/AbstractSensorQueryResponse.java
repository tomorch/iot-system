package iot.sensor.query.response;

public abstract class AbstractSensorQueryResponse {
    public static abstract class Status {
        public static class Success extends Status {
            public Success() {
                super(true);
            }
        }

        public static class Failure extends Status {
            private final String reason;

            public Failure(String reason) {
                super(false);

                this.reason = reason;
            }

            public String getReason() {
                return reason;
            }
        }

        private final boolean success;

        public Status(boolean success) {
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    private final Status status;

    public AbstractSensorQueryResponse(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
