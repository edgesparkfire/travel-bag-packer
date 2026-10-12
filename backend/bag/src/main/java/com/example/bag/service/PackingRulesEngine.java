package com.example.bag.service;

import com.example.bag.model.Item;
import com.example.bag.model.Trip;
import com.example.bag.model.WeatherForecast;
import org.springframework.stereotype.Service;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PackingRulesEngine {

    public List<Item> generatePackingList(Trip trip, WeatherForecast forecast) {
        List<Item> packingList = new ArrayList<>();
        int duration = calculateDurationDays(trip);

        String dest = (trip.getDestination() != null) ? trip.getDestination().toLowerCase() : "";
        if (trip.getDestinationCity() != null) dest += " " + trip.getDestinationCity().toLowerCase();
        String destCountry = (trip.getDestinationCountry() != null) ? trip.getDestinationCountry().toLowerCase() : "";

        boolean isPakistan = destCountry.contains("pakistan") || dest.contains("pakistan");
        boolean isIndia = !isPakistan && (destCountry.contains("india") || dest.contains("india") || 
                dest.contains("mumbai") || dest.contains("delhi") || 
                dest.contains("bengaluru") || dest.contains("jaipur") || dest.contains("goa") ||
                (dest.contains("hyderabad") && !dest.contains("pakistan")));

        String budgetTier = (trip.getBudgetTier() != null) ? trip.getBudgetTier().toUpperCase() : "BALANCED";

        // 1. CLOTHING RULES ACCORDING TO BUDGET TIER & DESTINATION
        if (budgetTier.equals("BUDGET")) {
            // Minimalist / Backpacker
            int topCount = Math.max(2, Math.min(4, duration / 2 + 1));
            packingList.add(createItem("Quick-Dry Washable T-Shirts / Tops", topCount, "Clothing", "Minimalist pack: sink-washable quick-dry shirts for budget rotation"));
            packingList.add(createItem("Versatile Lightweight Cargo / Convertible Pants", 2, "Clothing", "Durable pants suitable for transit and day walks"));
            packingList.add(createItem("Compact Fast-Dry Microfiber Towel", 1, "Essentials", "Hostel and sleeper coach essential"));
            packingList.add(createItem("Combination Padlock for Lockers", 1, "Essentials", "Secure luggage in budget accommodation lockers"));
        } else if (budgetTier.equals("LUXURY")) {
            // Executive / Luxury Comfort
            packingList.add(createItem("Tailored Breathable Linen Shirts & Blouses", duration, "Clothing", "Premium crisp linen attire for day touring and meetings"));
            packingList.add(createItem("Designer Casual Trousers & Chinos", Math.max(2, duration / 2 + 1), "Clothing", "High-thread-count comfortable pants"));
            packingList.add(createItem("Formal Evening Attire / Cocktail Dresses", Math.min(3, Math.max(1, duration / 2)), "Clothing", "For fine dining, luxury lounges, and events"));
            packingList.add(createItem("Handheld Compact Garment Steamer", 1, "Electronics", "Keep high-end fabrics unwrinkled"));
            packingList.add(createItem("Luxury Travel Skincare & Grooming Kit", 1, "Essentials", "Hydrating mist, eye serum, and premium toiletries"));
            packingList.add(createItem("Premium Noise-Cancelling Headphones", 1, "Electronics", "Uninterrupted quiet in lounges and transit"));
        } else {
            // BALANCED STANDARD
            if (isPakistan) {
                packingList.add(createItem("Modest Breathable Cotton Shirts & Trousers / Kurta", duration, "Clothing", "Modest loose cotton clothing suitable for warm dry climate"));
                packingList.add(createItem("Versatile Casual Trousers / Chinos", Math.max(2, (duration / 2) + 1), "Clothing", "Comfortable modest bottoms for bazaar visits"));
            } else if (isIndia) {
                packingList.add(createItem("Lightweight Breathable Cotton Tops / Shirts", duration, "Clothing", "1 daily change in breathable 100% cotton for tropical climate"));
                packingList.add(createItem("Loose Cotton Trousers / Linen Pants", Math.max(2, (duration / 2) + 1), "Clothing", "Modest coverage for street and temple visits"));
            } else {
                packingList.add(createItem("Shirts / Everyday Tops", duration, "Clothing", "1 daily change for " + duration + " days"));
                packingList.add(createItem("Trousers / Jeans / Bottoms", Math.max(1, (duration / 2) + 1), "Clothing", "Versatile pants for rotation"));
            }
            if (duration >= 2) {
                int dressCount = Math.min(3, Math.max(1, duration / 3));
                packingList.add(createItem("Evening Outfits / Smart Casual Wear", dressCount, "Clothing", "For dining out, heritage restaurants, and photos"));
            }
        }

        // Cultural & Etiquette Footwear / Coverings
        if (isPakistan) {
            packingList.add(createItem("Light Scarf / Dupatta / Shawl (Head & Shoulder Cover)", 1, "Clothing", "Mandatory etiquette for mosques, shrines, and traditional bazaars"));
            packingList.add(createItem("Slip-On Shoes / Loafers", 1, "Clothing", "Easily removed before entering shrines and carpeted heritage sites"));
        } else if (isIndia) {
            packingList.add(createItem("Light Scarf / Shawl (Head & Shoulder Cover)", 1, "Clothing", "Mandatory etiquette for temples, gurdwaras, and mosques"));
            packingList.add(createItem("Easy Slip-On Walking Shoes / Loafers", 1, "Clothing", "Footwear is removed frequently at temples and heritage sites"));
        } else {
            packingList.add(createItem("Comfortable Walking Shoes / Sneakers", 1, "Clothing", "Essential for exploring and daily transit"));
        }

        int underwearCount = budgetTier.equals("BUDGET") ? Math.min(4, duration) : (duration + 1);
        packingList.add(createItem("Socks & Hosiery", Math.min(duration, 5), "Clothing", "Fresh pairs for walking"));
        packingList.add(createItem("Underwear & Innerwear", underwearCount, "Clothing", "Daily essentials"));
        packingList.add(createItem("Comfortable Sleepwear / Loungewear", 1, "Clothing", "For overnight rest"));

        // 2. ESSENTIAL HYGIENE & TOILETRIES
        packingList.add(createItem("Toothbrush & Toothpaste", 1, "Essentials", "Daily oral care"));
        packingList.add(createItem("Travel Shampoo & Body Wash", 1, "Essentials", "Personal hygiene"));
        packingList.add(createItem("Deodorant & Fragrance", 1, "Essentials", "All-day freshness"));
        packingList.add(createItem("Mini First-Aid Kit & Pain Reliever", 1, "Essentials", "Band-aids, headache and stomach relief"));

        if (isPakistan) {
            packingList.add(createItem("Pocket Hand Sanitizer & Antiseptic Wet Wipes", 2, "Essentials", "Hand hygiene before meals and after transit"));
            packingList.add(createItem("Electrolyte Hydration Sachets (ORS)", 4, "Essentials", "Rapid rehydration in warm dry climate"));
            packingList.add(createItem("Mosquito Repellent Cream (Mospel)", 1, "Essentials", "Protection during evening sightseeing"));
        } else if (isIndia) {
            packingList.add(createItem("Mosquito Repellent Lotion (Odomos / DEET)", 1, "Essentials", "Protection during evening sightseeing"));
            packingList.add(createItem("Electrolyte Hydration Sachets (ORS)", 4, "Essentials", "Rapid rehydration in warm climate"));
            packingList.add(createItem("Pocket Hand Sanitizer & Antiseptic Wet Wipes", 2, "Essentials", "Indispensable before meals and transit"));
        }

        // 3. ELECTRONICS & POWER
        packingList.add(createItem("Phone Charger & High-Capacity Power Bank", 1, "Electronics", "Keep devices running while navigating (carry in cabin bag)"));
        if (isPakistan) {
            packingList.add(createItem("Universal Type C / D Travel Adapter (230V 50Hz)", 1, "Electronics", "Ensure grounded socket compatibility in Pakistan"));
        } else if (isIndia) {
            packingList.add(createItem("Type D / M Universal Travel Adapter (230V 3-Pin)", 1, "Electronics", "Ensure grounded socket compatibility for India"));
        } else {
            packingList.add(createItem("Universal Multi-Country Power Adapter", 1, "Electronics", "Ensure socket compatibility"));
        }
        packingList.add(createItem("Government ID / Passport / Tickets", 1, "Essentials", "Mandatory check-in identification"));

        // 4. TRANSPORT SPECIFIC RULES
        String transport = (trip.getTransportMode() != null) ? trip.getTransportMode().toUpperCase() : "TRAIN";
        if (transport.contains("TRAIN")) {
            packingList.add(createItem("Ergonomic Travel Neck Pillow", 1, "Transport Gear", "Comfortable sleep in train coach"));
            packingList.add(createItem("Light AC Blanket / Travel Shawl", 1, "Transport Gear", "AC coaches often become chilly"));
            packingList.add(createItem("Offline Railway e-Ticket PDF & Photo ID", 1, "Transport Gear", "Quick verification by ticket examiner"));
            if (isIndia || isPakistan) {
                packingList.add(createItem("Wire Luggage Lock & Chain", 1, "Transport Gear", "Secure suitcase under berth for overnight trains"));
            }
        } else if (transport.contains("FLIGHT") || transport.contains("AIR")) {
            packingList.add(createItem("100ml Liquid Travel Bottles (Clear Pouch)", 1, "Transport Gear", "TSA and Airport security compliance"));
            packingList.add(createItem("Portable Digital Luggage Scale", 1, "Transport Gear", "Avoid excess baggage fees at the airport"));
            packingList.add(createItem("Passport & Boarding Pass Wallet", 1, "Transport Gear", "Fast document access at security and gates"));
        } else if (transport.contains("CAR") || transport.contains("ROAD")) {
            packingList.add(createItem("Fast Car USB Charger & Phone Mount", 1, "Transport Gear", "GPS navigation and phone battery support"));
            packingList.add(createItem("Motion Sickness Pills (Dimenhydrinate)", 1, "Transport Gear", "For winding roads and passenger comfort"));
        } else if (transport.contains("BUS")) {
            packingList.add(createItem("Contoured Sleep Eye Mask & Foam Earplugs", 1, "Transport Gear", "Restful sleep on overnight sleeper bus"));
            packingList.add(createItem("Compact Anti-theft Crossbody Bag", 1, "Transport Gear", "Keep wallet and phone secure by your seat"));
        }

        // 5. WEATHER-ADAPTIVE RULES
        if (forecast != null) {
            if (forecast.isRain() || (forecast.getCondition() != null && forecast.getCondition().toLowerCase().contains("rain"))) {
                packingList.add(createItem("Compact Windproof Umbrella", 1, "Weather Gear", "Rain predicted (" + forecast.getCondition() + ")"));
                packingList.add(createItem("Lightweight Waterproof Rain Jacket", 1, "Weather Gear", "Stay dry during sudden city showers"));
            }

            if (forecast.getTemperature() < 15.0) {
                packingList.add(createItem("Insulated Warm Coat / Puffer Jacket", 1, "Weather Gear", "Chilly weather (" + forecast.getTemperature() + "C)"));
                packingList.add(createItem("Fleece Hoodie / Pullover", 1, "Weather Gear", "Layering piece for morning and evening"));
            }

            if (forecast.getTemperature() >= 24.0) {
                packingList.add(createItem("UV400 Sunglasses", 1, "Weather Gear", "Protect eyes in sunny outdoor sightseeing"));
                packingList.add(createItem("Broad Spectrum Sunscreen SPF 50+", 1, "Weather Gear", "Skin protection against high UV index"));
            }
        }

        return packingList;
    }

    private int calculateDurationDays(Trip trip) {
        if (trip.getDurationDays() != null && trip.getDurationDays() > 0) {
            return trip.getDurationDays();
        }

        if (trip.getStartDate() != null && trip.getEndDate() != null) {
            long days = ChronoUnit.DAYS.between(trip.getStartDate(), trip.getEndDate());
            return Math.max(1, (int) days + 1);
        }

        return 3;
    }

    private Item createItem(String name, int quantity, String category, String reason) {
        return Item.builder()
                .itemId(UUID.randomUUID().toString())
                .itemName(name)
                .quantity(quantity)
                .isPacked(false)
                .category(category)
                .reason(reason)
                .build();
    }
}
