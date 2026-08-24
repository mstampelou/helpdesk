package gr.aueb.cf.helpdesk.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(String uuid) {
        super("Attachment not found: " + uuid);
    }
}