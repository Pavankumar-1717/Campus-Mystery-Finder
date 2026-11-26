package com.campus.lostfound;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.service.MatchingEngineService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingEngineServiceTest {

    private final MatchingEngineService service = new MatchingEngineService();

    @Test
    public void testHighConfidenceMatch() {
        Item item = new Item();
        item.setName("Boat Wireless Earbuds");
        item.setColour("Matte Black");
        item.setSize("Small");
        item.setIsElectronic(true);
        item.setLocation("Central Library 2nd Floor");
        item.setBrand("Boat");
        item.setSecretClue("Yellow smiley sticker on back");

        Claim claim = new Claim();
        claim.setItemName("Boat Wireless Earbuds");
        claim.setClaimColour("Black");
        claim.setClaimSize("Small");
        claim.setClaimIsElectronic(true);
        claim.setClaimLocation("Library 2nd Floor");
        claim.setClaimBrand("Boat");
        claim.setSecretAnswer("Has a yellow smiley sticker");

        MatchingEngineService.MatchBreakdown breakdown = service.evaluateClaim(claim, item);

        assertTrue(breakdown.getTotalScore() >= 75, "Score should be >= 75 for high match");
        assertEquals("HIGH", breakdown.getConfidenceLevel());
    }

    @Test
    public void testLowConfidenceMismatch() {
        Item item = new Item();
        item.setName("Stainless Steel Bottle");
        item.setColour("Silver");
        item.setSize("Medium");
        item.setIsElectronic(false);
        item.setLocation("Gymnasium");
        item.setBrand("Milton");
        item.setSecretClue("Initials RK engraved");

        Claim claim = new Claim();
        claim.setItemName("Stainless Steel Bottle");
        claim.setClaimColour("Red");
        claim.setClaimSize("Big");
        claim.setClaimIsElectronic(true); // wrong!
        claim.setClaimLocation("Canteen");
        claim.setClaimBrand("Tupperware");
        claim.setSecretAnswer("Blue cap with handle");

        MatchingEngineService.MatchBreakdown breakdown = service.evaluateClaim(claim, item);

        assertTrue(breakdown.getTotalScore() < 40, "Score should be low for mismatch");
        assertEquals("LOW", breakdown.getConfidenceLevel());
    }
}
