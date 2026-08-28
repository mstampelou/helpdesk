package gr.aueb.cf.helpdesk.service;

import gr.aueb.cf.helpdesk.dto.AttachmentReadOnlyDTO;
import gr.aueb.cf.helpdesk.model.Attachment;
import gr.aueb.cf.helpdesk.model.Category;
import gr.aueb.cf.helpdesk.model.Ticket;
import gr.aueb.cf.helpdesk.model.User;
import gr.aueb.cf.helpdesk.repository.AttachmentRepository;
import gr.aueb.cf.helpdesk.repository.TicketRepository;
import gr.aueb.cf.helpdesk.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AttachmentServiceImplTest {

    @Mock
    private AttachmentRepository attachmentRepository;
    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AttachmentServiceImpl attachmentService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(attachmentService, "uploadDir", tempDir.toString());
    }

    @Test
    void uploadAttachment_shouldThrow_whenFileIsEmpty() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThrows(IllegalArgumentException.class,
                () -> attachmentService.uploadAttachment("t-uuid", emptyFile, "user.demo"));
    }

    @Test
    void uploadAttachment_shouldThrow_whenFileTooLarge() {
        byte[] tooLarge = new byte[6 * 1024 * 1024]; // 6MB, όριο είναι 5MB
        MockMultipartFile bigFile = new MockMultipartFile("file", "big.png", "image/png", tooLarge);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> attachmentService.uploadAttachment("t-uuid", bigFile, "user.demo"));
        assertTrue(ex.getMessage().contains("too large"));
    }

    @Test
    void uploadAttachment_shouldThrow_whenContentTypeNotAllowed() {
        MockMultipartFile exeFile = new MockMultipartFile("file", "virus.exe", "application/x-msdownload", "fake content".getBytes());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> attachmentService.uploadAttachment("t-uuid", exeFile, "user.demo"));
        assertTrue(ex.getMessage().contains("not allowed"));
    }

    @Test
    void uploadAttachment_shouldSucceed_whenFileIsValid() {
        Category category = new Category();
        category.setName("Hardware");

        Ticket ticket = new Ticket();
        ticket.setCategory(category);

        User uploader = new User();
        uploader.setUsername("user.demo");
        uploader.setFullName("Alex Doukas");

        MockMultipartFile validFile = new MockMultipartFile("file", "screenshot.png", "image/png", "fake image bytes".getBytes());

        when(ticketRepository.findByUuidAndDeletedFalse("t-uuid")).thenReturn(Optional.of(ticket));
        when(userRepository.findByUsername("user.demo")).thenReturn(Optional.of(uploader));
        when(attachmentRepository.save(any(Attachment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AttachmentReadOnlyDTO result = attachmentService.uploadAttachment("t-uuid", validFile, "user.demo");

        assertEquals("screenshot.png", result.getFileName());
        assertEquals("image/png", result.getContentType());
        assertEquals("Alex Doukas", result.getUploadedByName());
    }
}