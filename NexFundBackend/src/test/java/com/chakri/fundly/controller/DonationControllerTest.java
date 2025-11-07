package com.chakri.fundly.controller;

import com.chakri.fundly.model.Donation;
import com.chakri.fundly.service.DonationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DonationController Unit Tests")
class DonationControllerTest {

    @Mock
    private DonationService donationService;

    @InjectMocks
    private DonationController donationController;

    private Donation testDonation;
    private Donation testDonation2;
    private String testDonationId;

    @BeforeEach
    void setUp() {
        // NOTE: Do NOT call MockitoAnnotations.openMocks(this) when using @ExtendWith(MockitoExtension.class)
        // The extension automatically handles mock initialization

        // Arrange: Set up test data
        testDonationId = "550e8400-e29b-41d4-a716-446655440000";

        testDonation = new Donation();
        testDonation.setId(testDonationId);
        testDonation.setTitle("Medical Fund");
        testDonation.setDescription("Help for medical expenses");
        testDonation.setUpiId("user@upi");
        testDonation.setUpiMsg("Donation for medical fund");
        testDonation.setCreatorUsername("johndoe");
        testDonation.setImageDataUrl("data:image/png;base64,iVBORw0KGgo...");
        testDonation.setAmount(5000);

        testDonation2 = new Donation();
        testDonation2.setId("660e8400-e29b-41d4-a716-446655440001");
        testDonation2.setTitle("Education Fund");
        testDonation2.setDescription("Help for education");
        testDonation2.setUpiId("user2@upi");
        testDonation2.setUpiMsg("Donation for education");
        testDonation2.setCreatorUsername("janedoe");
        testDonation2.setImageDataUrl("data:image/png;base64,iVBORw0KGgo...");
        testDonation2.setAmount(10000);
    }

    // ======================== POST /donations/create Tests ========================

    @Test
    @DisplayName("POST /create - Should successfully create a new donation with valid data")
    void testCreateDonation_Success() {
        // Arrange
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(testDonation);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(testDonationId, response.getBody().getId(), "Donation ID should match");
        assertEquals("Medical Fund", response.getBody().getTitle(), "Title should match");
        assertEquals(5000, response.getBody().getAmount(), "Amount should match");
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should create donation with minimal data (only required fields)")
    void testCreateDonation_MinimalData() {
        // Arrange
        Donation minimalDonation = new Donation();
        minimalDonation.setId("test-id");
        minimalDonation.setTitle("Minimal Fund");
        when(donationService.createDonation(any(Donation.class))).thenReturn(minimalDonation);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(minimalDonation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Minimal Fund", response.getBody().getTitle());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should create donation with null optional fields")
    void testCreateDonation_WithNullOptionalFields() {
        // Arrange
        Donation donationWithNulls = new Donation();
        donationWithNulls.setId("test-id-null");
        donationWithNulls.setTitle("Fund with nulls");
        donationWithNulls.setDescription(null);
        donationWithNulls.setUpiId(null);
        donationWithNulls.setImageDataUrl(null);
        when(donationService.createDonation(any(Donation.class))).thenReturn(donationWithNulls);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(donationWithNulls);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getDescription());
        assertNull(response.getBody().getUpiId());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should create donation with zero amount")
    void testCreateDonation_WithZeroAmount() {
        // Arrange
        Donation donationZeroAmount = new Donation();
        donationZeroAmount.setId("zero-amount-id");
        donationZeroAmount.setTitle("Zero Amount Fund");
        donationZeroAmount.setAmount(0);
        when(donationService.createDonation(any(Donation.class))).thenReturn(donationZeroAmount);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(donationZeroAmount);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getAmount());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should create donation with large amount")
    void testCreateDonation_WithLargeAmount() {
        // Arrange
        Donation largeDonation = new Donation();
        largeDonation.setId("large-id");
        largeDonation.setTitle("Large Fund");
        largeDonation.setAmount(Integer.MAX_VALUE);
        when(donationService.createDonation(any(Donation.class))).thenReturn(largeDonation);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(largeDonation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Integer.MAX_VALUE, response.getBody().getAmount());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should create donation with empty strings")
    void testCreateDonation_WithEmptyStrings() {
        // Arrange
        Donation donationEmptyStrings = new Donation();
        donationEmptyStrings.setId("");
        donationEmptyStrings.setTitle("");
        donationEmptyStrings.setDescription("");
        donationEmptyStrings.setUpiId("");
        when(donationService.createDonation(any(Donation.class))).thenReturn(donationEmptyStrings);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(donationEmptyStrings);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("", response.getBody().getTitle());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("POST /create - Should handle long text fields correctly")
    void testCreateDonation_WithLongTextFields() {
        // Arrange
        String longText = "a".repeat(1000);
        Donation donationLongText = new Donation();
        donationLongText.setId("long-text-id");
        donationLongText.setTitle(longText);
        donationLongText.setDescription(longText);
        when(donationService.createDonation(any(Donation.class))).thenReturn(donationLongText);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(donationLongText);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(longText, response.getBody().getTitle());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    // ======================== GET /donations/all Tests ========================

    @Test
    @DisplayName("GET /all - Should retrieve all donations successfully")
    void testGetAllDonations_Success() {
        // Arrange
        List<Donation> donationsList = new ArrayList<>();
        donationsList.add(testDonation);
        donationsList.add(testDonation2);
        when(donationService.getAllDonations()).thenReturn(donationsList);

        // Act
        List<Donation> response = donationController.getAllDonations();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(2, response.size(), "Should return 2 donations");
        assertEquals("Medical Fund", response.get(0).getTitle(), "First donation title should match");
        assertEquals("Education Fund", response.get(1).getTitle(), "Second donation title should match");
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    @DisplayName("GET /all - Should return empty list when no donations exist")
    void testGetAllDonations_EmptyList() {
        // Arrange
        List<Donation> emptyList = new ArrayList<>();
        when(donationService.getAllDonations()).thenReturn(emptyList);

        // Act
        List<Donation> response = donationController.getAllDonations();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertTrue(response.isEmpty(), "Response should be empty");
        assertEquals(0, response.size(), "Size should be 0");
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    @DisplayName("GET /all - Should retrieve single donation from list")
    void testGetAllDonations_SingleDonation() {
        // Arrange
        List<Donation> singleDonationList = new ArrayList<>();
        singleDonationList.add(testDonation);
        when(donationService.getAllDonations()).thenReturn(singleDonationList);

        // Act
        List<Donation> response = donationController.getAllDonations();

        // Assert
        assertEquals(1, response.size(), "Should return 1 donation");
        assertEquals(testDonationId, response.get(0).getId(), "Donation ID should match");
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    @DisplayName("GET /all - Should retrieve large number of donations")
    void testGetAllDonations_LargeList() {
        // Arrange
        List<Donation> largeList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Donation donation = new Donation();
            donation.setId("donation-" + i);
            donation.setTitle("Fund " + i);
            largeList.add(donation);
        }
        when(donationService.getAllDonations()).thenReturn(largeList);

        // Act
        List<Donation> response = donationController.getAllDonations();

        // Assert
        assertEquals(100, response.size(), "Should return 100 donations");
        assertEquals("donation-0", response.get(0).getId());
        assertEquals("donation-99", response.get(99).getId());
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    @DisplayName("GET /all - Should verify service method is called exactly once")
    void testGetAllDonations_VerifyServiceCall() {
        // Arrange
        when(donationService.getAllDonations()).thenReturn(new ArrayList<>());

        // Act
        donationController.getAllDonations();

        // Assert
        verify(donationService, times(1)).getAllDonations();
        verifyNoMoreInteractions(donationService);
    }

    // ======================== GET /donations/{id} Tests ========================

    @Test
    @DisplayName("GET /{id} - Should retrieve donation by ID when exists")
    void testGetDonationById_Found() {
        // Arrange
        when(donationService.getDonationById(testDonationId)).thenReturn(Optional.of(testDonation));

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById(testDonationId);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Status code should be 200 OK");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(testDonationId, response.getBody().getId(), "Donation ID should match");
        assertEquals("Medical Fund", response.getBody().getTitle(), "Title should match");
        verify(donationService, times(1)).getDonationById(testDonationId);
    }

    @Test
    @DisplayName("GET /{id} - Should return 404 when donation not found")
    void testGetDonationById_NotFound() {
        // Arrange
        String nonExistentId = "non-existent-id";
        when(donationService.getDonationById(nonExistentId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById(nonExistentId);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), "Status code should be 404");
        assertNull(response.getBody(), "Response body should be null");
        verify(donationService, times(1)).getDonationById(nonExistentId);
    }

    @Test
    @DisplayName("GET /{id} - Should handle null ID parameter gracefully")
    void testGetDonationById_NullId() {
        // Arrange
        when(donationService.getDonationById(null)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById(null);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).getDonationById(null);
    }

    @Test
    @DisplayName("GET /{id} - Should handle empty string ID")
    void testGetDonationById_EmptyId() {
        // Arrange
        when(donationService.getDonationById("")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById("");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).getDonationById("");
    }

    @Test
    @DisplayName("GET /{id} - Should handle special characters in ID")
    void testGetDonationById_SpecialCharacterId() {
        // Arrange
        String specialId = "id-with-@#$%^&*()";
        when(donationService.getDonationById(specialId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById(specialId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).getDonationById(specialId);
    }

    @Test
    @DisplayName("GET /{id} - Should handle very long ID string")
    void testGetDonationById_LongId() {
        // Arrange
        String longId = "a".repeat(1000);
        when(donationService.getDonationById(longId)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Donation> response = donationController.getDonationById(longId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).getDonationById(longId);
    }

    @Test
    @DisplayName("GET /{id} - Should return correct donation when multiple calls with different IDs")
    void testGetDonationById_MultipleCalls() {
        // Arrange
        when(donationService.getDonationById(testDonationId)).thenReturn(Optional.of(testDonation));
        when(donationService.getDonationById(testDonation2.getId())).thenReturn(Optional.of(testDonation2));

        // Act
        ResponseEntity<Donation> response1 = donationController.getDonationById(testDonationId);
        ResponseEntity<Donation> response2 = donationController.getDonationById(testDonation2.getId());

        // Assert
        assertEquals(HttpStatus.OK, response1.getStatusCode());
        assertEquals("Medical Fund", response1.getBody().getTitle());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertEquals("Education Fund", response2.getBody().getTitle());
        verify(donationService, times(1)).getDonationById(testDonationId);
        verify(donationService, times(1)).getDonationById(testDonation2.getId());
    }

    // ======================== DELETE /donations/{id} Tests ========================

    @Test
    @DisplayName("DELETE /{id} - Should successfully delete donation")
    void testDeleteDonation_Success() {
        // Arrange
        doNothing().when(donationService).deleteDonation(testDonationId);

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation(testDonationId);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(), "Status code should be 204 NO_CONTENT");
        assertNull(response.getBody(), "Response body should be null");
        verify(donationService, times(1)).deleteDonation(testDonationId);
    }

    @Test
    @DisplayName("DELETE /{id} - Should handle deletion of non-existent donation")
    void testDeleteDonation_NonExistent() {
        // Arrange
        String nonExistentId = "non-existent-id";
        doNothing().when(donationService).deleteDonation(nonExistentId);

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation(nonExistentId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).deleteDonation(nonExistentId);
    }

    @Test
    @DisplayName("DELETE /{id} - Should handle null ID parameter")
    void testDeleteDonation_NullId() {
        // Arrange
        doNothing().when(donationService).deleteDonation(null);

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation(null);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).deleteDonation(null);
    }

    @Test
    @DisplayName("DELETE /{id} - Should handle empty string ID")
    void testDeleteDonation_EmptyId() {
        // Arrange
        doNothing().when(donationService).deleteDonation("");

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation("");

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).deleteDonation("");
    }

    @Test
    @DisplayName("DELETE /{id} - Should handle special characters in ID")
    void testDeleteDonation_SpecialCharacterId() {
        // Arrange
        String specialId = "id-with-!@#$%";
        doNothing().when(donationService).deleteDonation(specialId);

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation(specialId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(donationService, times(1)).deleteDonation(specialId);
    }

    @Test
    @DisplayName("DELETE /{id} - Should handle multiple consecutive deletions")
    void testDeleteDonation_MultipleConsecutive() {
        // Arrange
        doNothing().when(donationService).deleteDonation(anyString());

        // Act
        ResponseEntity<Void> response1 = donationController.deleteDonation(testDonationId);
        ResponseEntity<Void> response2 = donationController.deleteDonation(testDonation2.getId());

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response1.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
        verify(donationService, times(2)).deleteDonation(anyString());
    }

    @Test
    @DisplayName("DELETE /{id} - Verify service method called with correct parameter")
    void testDeleteDonation_VerifyCorrectParameter() {
        // Arrange
        doNothing().when(donationService).deleteDonation(testDonationId);

        // Act
        donationController.deleteDonation(testDonationId);

        // Assert
        verify(donationService, times(1)).deleteDonation(testDonationId);
    }

    // ======================== Integration/Edge Cases Tests ========================

    @Test
    @DisplayName("Should handle rapid sequential create and retrieve")
    void testSequentialCreateAndRetrieve() {
        // Arrange
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);
        when(donationService.getDonationById(testDonationId)).thenReturn(Optional.of(testDonation));

        // Act
        ResponseEntity<Donation> createResponse = donationController.createDonation(testDonation);
        ResponseEntity<Donation> getResponse = donationController.getDonationById(testDonationId);

        // Assert
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(createResponse.getBody().getId(), getResponse.getBody().getId());
        verify(donationService, times(1)).createDonation(any(Donation.class));
        verify(donationService, times(1)).getDonationById(testDonationId);
    }

    @Test
    @DisplayName("Should handle create, retrieve, and delete sequence")
    void testCreateRetrieveDeleteSequence() {
        // Arrange
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);
        when(donationService.getDonationById(testDonationId)).thenReturn(Optional.of(testDonation));
        doNothing().when(donationService).deleteDonation(testDonationId);

        // Act
        donationController.createDonation(testDonation);
        ResponseEntity<Donation> getResponse = donationController.getDonationById(testDonationId);
        ResponseEntity<Void> deleteResponse = donationController.deleteDonation(testDonationId);

        // Assert
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        verify(donationService, times(1)).createDonation(any(Donation.class));
        verify(donationService, times(1)).getDonationById(testDonationId);
        verify(donationService, times(1)).deleteDonation(testDonationId);
    }

    @Test
    @DisplayName("Should verify no interactions with service when no methods called")
    void testNoServiceInteraction() {
        // Assert - verifies mocks haven't been interacted with yet
        verifyNoInteractions(donationService);
    }

    @Test
    @DisplayName("Should verify service is called correct number of times")
    void testServiceCallCount() {
        // Arrange
        List<Donation> emptyList = new ArrayList<>();
        when(donationService.getAllDonations()).thenReturn(emptyList);

        // Act
        donationController.getAllDonations();
        donationController.getAllDonations();
        donationController.getAllDonations();

        // Assert
        verify(donationService, times(3)).getAllDonations();
    }

    @Test
    @DisplayName("Should handle donations with all fields populated")
    void testDonationWithAllFieldsPopulated() {
        // Arrange
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);

        // Act
        ResponseEntity<Donation> response = donationController.createDonation(testDonation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Medical Fund", response.getBody().getTitle());
        assertEquals("Help for medical expenses", response.getBody().getDescription());
        assertEquals("user@upi", response.getBody().getUpiId());
        assertEquals("johndoe", response.getBody().getCreatorUsername());
        assertEquals(5000, response.getBody().getAmount());
        verify(donationService, times(1)).createDonation(any(Donation.class));
    }

    @Test
    @DisplayName("Should verify exact parameter passed to service method")
    void testVerifyExactParameters() {
        // Arrange
        Donation expectedDonation = new Donation();
        expectedDonation.setTitle("Test Fund");
        when(donationService.createDonation(any(Donation.class))).thenReturn(expectedDonation);

        // Act
        donationController.createDonation(expectedDonation);

        // Assert - verify that the service was called with the expected donation
        verify(donationService).createDonation(expectedDonation);
    }

    @Test
    @DisplayName("GET /all - Should handle donations with null values in list")
    void testGetAllDonations_WithVariousStates() {
        // Arrange
        Donation fullDonation = testDonation;
        Donation minimalDonation = new Donation();
        minimalDonation.setId("minimal-id");
        minimalDonation.setTitle("Minimal");

        List<Donation> mixedList = new ArrayList<>();
        mixedList.add(fullDonation);
        mixedList.add(minimalDonation);

        when(donationService.getAllDonations()).thenReturn(mixedList);

        // Act
        List<Donation> response = donationController.getAllDonations();

        // Assert
        assertEquals(2, response.size());
        assertEquals("Medical Fund", response.get(0).getTitle());
        assertEquals("Minimal", response.get(1).getTitle());
        verify(donationService, times(1)).getAllDonations();
    }

    @Test
    @DisplayName("Should handle response entity status codes correctly for all endpoints")
    void testResponseEntityStatusCodes() {
        // Arrange
        when(donationService.createDonation(any(Donation.class))).thenReturn(testDonation);
        when(donationService.getDonationById(testDonationId)).thenReturn(Optional.of(testDonation));
        when(donationService.getDonationById("missing-id")).thenReturn(Optional.empty());
        doNothing().when(donationService).deleteDonation(testDonationId);

        // Act & Assert
        assertEquals(HttpStatus.OK, donationController.createDonation(testDonation).getStatusCode());
        assertEquals(HttpStatus.OK, donationController.getDonationById(testDonationId).getStatusCode());
        assertEquals(HttpStatus.NOT_FOUND, donationController.getDonationById("missing-id").getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, donationController.deleteDonation(testDonationId).getStatusCode());
    }

    @Test
    @DisplayName("DELETE /{id} - Should return NO_CONTENT without body")
    void testDeleteDonation_ResponseStructure() {
        // Arrange
        doNothing().when(donationService).deleteDonation(testDonationId);

        // Act
        ResponseEntity<Void> response = donationController.deleteDonation(testDonationId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        assertTrue(response.getStatusCodeValue() == 204);
    }
}
