package com.chakri.fundly.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mockMailSender;

    @Mock
    private MimeMessage mockMimeMessage;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendVerificationEmail_Success_WithValidInput() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("participant@example.com", "John Doe", "Build Community School", "Admin User", "5000");
        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_Success_WithDifferentValidData() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("test@domain.com", "Alice Smith", "Medical Emergency Fund", "Verifier", "10000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_Success_MailSenderCalledOnce() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "Test User", "Test Fundraiser", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendVerificationEmail_NullParticipantName() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", null, "Fundraiser", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_NullFundraiserTitle() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", null, "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_NullVerifiedBy() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", null, "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_NullAmountPaid() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", null);
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_EmptyParticipantName() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "", "Fundraiser", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_EmptyFundraiserTitle() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_EmptyVerifiedBy() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_EmptyAmountPaid() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", "");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_SpecialCharactersInInput() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user+test@example.com", "John O'Doe & Co.", "Build School", "Verifier/Admin", "5000.50");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_UnicodeCharacters() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "राजकुमार", "学校建設基金", "مدقق", "₹5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_LongStringInputs() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        String longString = "a".repeat(500);
        emailService.sendVerificationEmail("user@test.com", longString, longString, longString, "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_ZeroAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", "0");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_NegativeAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", "-5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_DecimalAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", "5000.99");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_VeryLargeAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fundraiser", "Verifier", "999999999999");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_MultipleSequentialCalls() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user1@test.com", "User1", "Fund1", "Ver1", "5000");
        emailService.sendVerificationEmail("user2@test.com", "User2", "Fund2", "Ver2", "10000");
        emailService.sendVerificationEmail("user3@test.com", "User3", "Fund3", "Ver3", "15000");
        verify(mockMailSender, times(3)).send(mockMimeMessage);
        verify(mockMailSender, times(3)).createMimeMessage();
    }

    @Test
    void testSendVerificationEmail_VerifyCreateMessageCalled() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendVerificationEmail_VerifySendCalled() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_VerifyNoExtraInteractions() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send(mockMimeMessage);
        verifyNoMoreInteractions(mockMailSender);
    }

    @Test
    void testSendVerificationEmail_AllParametersValid() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("complete@test.com", "Complete Test", "Complete Fundraiser", "Complete Verifier", "99999");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
        verify(mockMailSender, times(1)).createMimeMessage();
    }

    @Test
    void testSendVerificationEmail_MinimalParameters() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("a@b.c", "A", "B", "C", "1");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_MixedNullAndEmptyParameters() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", null, "", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_StateResetAfterSuccess() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user1@test.com", "User1", "Fund1", "Ver1", "5000");
        emailService.sendVerificationEmail("user2@test.com", "User2", "Fund2", "Ver2", "5000");
        verify(mockMailSender, times(2)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_CorrectMimeMessageUsed() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
        MimeMessage differentMessage = mock(MimeMessage.class);
        verify(mockMailSender, never()).send(differentMessage);
    }

    @Test
    void testSendVerificationEmail_ConsecutiveCallsIsolated() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user1@test.com", "User1", "Fund1", "Ver1", "1000");
        emailService.sendVerificationEmail("user2@test.com", "User2", "Fund2", "Ver2", "2000");
        emailService.sendVerificationEmail("user3@test.com", "User3", "Fund3", "Ver3", "3000");
        verify(mockMailSender, times(3)).send(mockMimeMessage);
        verify(mockMailSender, times(3)).createMimeMessage();
    }

    @Test
    void testSendVerificationEmail_NumericStringAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "123456789");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_AlphanumericAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000ABC");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_CurrencySymbolAmount() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "$5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_VerifyMockBehaviorConsistency() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender).createMimeMessage();
        verify(mockMailSender).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_VerifyArgumentMatcherUsage() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("test@example.com", "TestName", "TestFund", "TestVer", "1000");
        verify(mockMailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void testSendVerificationEmail_SuccessfulExecutionFlow() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("success@test.com", "SuccessName", "SuccessFund", "SuccessVer", "7500");
        verify(mockMailSender, times(1)).createMimeMessage();
        verify(mockMailSender, times(1)).send(mockMimeMessage);
        verifyNoMoreInteractions(mockMailSender);
    }

    @Test
    void testSendVerificationEmail_WithValidEmail() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("valid.email@test.com", "Participant", "Fundraiser", "Verifier", "1000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_DifferentVerifiers() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Admin", "5000");
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Manager", "5000");
        verify(mockMailSender, times(2)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_LargeNumberAmounts() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "999999999");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_MultipleEmails() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        for (int i = 0; i < 5; i++) {
            emailService.sendVerificationEmail("user" + i + "@test.com", "User" + i, "Fund" + i, "Verifier" + i, String.valueOf(1000 * (i + 1)));
        }
        verify(mockMailSender, times(5)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_SameParametersMultipleTimes() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("same@test.com", "Same Name", "Same Fund", "Verifier1", "5000");
        emailService.sendVerificationEmail("same@test.com", "Same Name", "Same Fund", "Verifier2", "5000");
        emailService.sendVerificationEmail("same@test.com", "Same Name", "Same Fund", "Verifier3", "5000");
        verify(mockMailSender, times(3)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_VariousAmountFormats() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "100");
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "1000.50");
        emailService.sendVerificationEmail("user@test.com", "John", "Fund", "Verifier", "5000000");
        verify(mockMailSender, times(3)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_WithSpecialEmailFormats() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user.name+tag@test.co.uk", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_WithLongFundraiserName() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        String longName = "This is a very long fundraiser title that contains many words to test the system";
        emailService.sendVerificationEmail("user@test.com", "John", longName, "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_SingleCharacterInputs() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("a@b.c", "b", "c", "d", "1");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_MixedCaseEmails() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("User@TEST.COM", "John", "Fund", "Verifier", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }

    @Test
    void testSendVerificationEmail_NumbersInNames() throws MessagingException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        emailService.sendVerificationEmail("user123@test.com", "John123", "Fund456", "Verifier789", "5000");
        verify(mockMailSender, times(1)).send(mockMimeMessage);
    }
}
