package com.capstone.realestate.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseAndCriteriaTest {

    @Test
    void successFactory_ShouldMarkSuccessTrue() {
        ApiResponse<String> res = ApiResponse.success("ok", "data");
        assertTrue(res.isSuccess());
    }

    @Test
    void successFactory_ShouldKeepMessage() {
        ApiResponse<String> res = ApiResponse.success("done", "x");
        assertEquals("done", res.getMessage());
    }

    @Test
    void successFactory_ShouldKeepData() {
        ApiResponse<String> res = ApiResponse.success("ok", "payload");
        assertEquals("payload", res.getData());
    }

    @Test
    void errorFactory_ShouldMarkSuccessFalse() {
        ApiResponse<String> res = ApiResponse.error("bad");
        assertFalse(res.isSuccess());
    }

    @Test
    void errorFactory_ShouldHaveNullData() {
        ApiResponse<String> res = ApiResponse.error("bad");
        assertNull(res.getData());
    }

    @Test
    void errorFactory_ShouldKeepMessage() {
        ApiResponse<String> res = ApiResponse.error("oops");
        assertEquals("oops", res.getMessage());
    }

    @Test
    void criteriaConstructor_ShouldAssignAllFields() {
        PropertyCriteria c = new PropertyCriteria("2BHK", "SALE", "Chennai", 10, 20);
        assertEquals("2BHK", c.getConfig());
    }

    @Test
    void criteriaConstructor_ShouldAssignOffer() {
        PropertyCriteria c = new PropertyCriteria("2BHK", "RENT", "Chennai", 10, 20);
        assertEquals("RENT", c.getOffer());
    }

    @Test
    void criteriaSetter_ShouldUpdateCity() {
        PropertyCriteria c = new PropertyCriteria();
        c.setCity("Madurai");
        assertEquals("Madurai", c.getCity());
    }

    @Test
    void criteriaSetter_ShouldUpdateMinCost() {
        PropertyCriteria c = new PropertyCriteria();
        c.setMinCost(123.5);
        assertEquals(123.5, c.getMinCost());
    }

    @Test
    void criteriaSetter_ShouldUpdateMaxCost() {
        PropertyCriteria c = new PropertyCriteria();
        c.setMaxCost(999.9);
        assertEquals(999.9, c.getMaxCost());
    }

    @Test
    void authDtoRegisterRequest_ShouldStoreValues() {
        AuthDTO.RegisterRequest req = new AuthDTO.RegisterRequest();
        req.setEmail("u@example.com");
        req.setRole("CUSTOMER");
        assertEquals("u@example.com", req.getEmail());
    }
}
