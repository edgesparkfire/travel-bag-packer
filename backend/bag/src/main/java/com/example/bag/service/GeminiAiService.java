package com.example.bag.service;

import com.example.bag.model.Trip;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GeminiAiService {

    public Map<String, Object> getAiTravelRecommendations(String originCity, String destination, int durationDays, String transportMode) {
        String transport = (transportMode != null && !transportMode.trim().isEmpty()) ? transportMode.toUpperCase() : "TRAIN";
        String destCity = (destination != null && !destination.trim().isEmpty()) ? destination : "Your Destination";
        String origCity = (originCity != null && !originCity.trim().isEmpty()) ? originCity : "Your Home City";

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("origin", origCity);
        result.put("destination", destCity);
        result.put("durationDays", durationDays);
        result.put("transportMode", transport);
        result.put("generatedBy", "Google Gemini AI Travel Engine");

        String destLower = destCity.toLowerCase();
        boolean isIndiaDest = destLower.contains("india") ||
                destLower.contains("hyderabad") ||
                destLower.contains("mumbai") ||
                destLower.contains("delhi") ||
                destLower.contains("bangalore") ||
                destLower.contains("bengaluru") ||
                destLower.contains("jaipur") ||
                destLower.contains("goa");

        boolean isJapanDest = destLower.contains("japan") || destLower.contains("tokyo");
        boolean isUkDest = destLower.contains("london") || destLower.contains("uk");

        // 1. CLOTHING SPECIFIC AI ADVICE
        List<String> clothingAdvice = new ArrayList<>();
        if (isIndiaDest) {
            clothingAdvice.add("Fabric Selection: Pack 100% breathable pure cotton, linen, or moisture-wicking blends. Avoid heavy synthetics/polyester due to humidity and warmth.");
            clothingAdvice.add("Modesty & Cultural Respect: Pack tops and bottoms that cover shoulders, chest, and knees. Essential for entry into temples, mosques, and heritage forts.");
            clothingAdvice.add("Temple Visits: Carry a lightweight scarf/dupatta or shawl (women) and easy slip-on loafers/sandals without laces since shoes must be removed frequently.");
            clothingAdvice.add("Duration Rule: For " + durationDays + " days, pack " + Math.min(durationDays, 6) + " light cotton tops, " + Math.max(2, durationDays / 2) + " breathable trousers/cotton pants, and 1 smart casual evening outfit for heritage dining.");
        } else if (isJapanDest) {
            clothingAdvice.add("Footwear: Slip-on sneakers or clean loafers are crucial; you will take shoes off multiple times daily (tatami restaurants, shrines, fitting rooms).");
            clothingAdvice.add("Neat Minimalism: Pack smart-casual, unwrinkled neutral tones (navy, beige, black, cream). Clean presentation is culturally valued.");
            clothingAdvice.add("Layers: Weather can shift between outdoor gardens and air-conditioned subways; carry an ultra-light packable cardigan or windbreaker.");
        } else if (isUkDest) {
            clothingAdvice.add("Layering Strategy: Weather is notoriously fickle. Always pack a water-resistant trench or lightweight waterproof jacket.");
            clothingAdvice.add("Walking Comfort: London requires 15,000+ steps daily; wear broken-in waterproof walking shoes.");
        } else {
            clothingAdvice.add("Versatile Capsule Wardrobe: Pack neutral-colored bottoms that pair with multiple lightweight shirts.");
            clothingAdvice.add("Weather Readiness: Check the local forecast 24 hours prior to departure for temperature drops.");
        }

        // 2. ELECTRONICS & VOLTAGE AI ADVICE
        List<String> electronicsAdvice = new ArrayList<>();
        if (isIndiaDest) {
            electronicsAdvice.add("Voltage & Frequency: India operates on 230V, 50Hz. If coming from the US (120V), verify your chargers say 'Input: 100-240V'. Hair dryers/curling irons require dual-voltage or a power converter.");
            electronicsAdvice.add("Socket Standard: Type D (3 round pins) and Type M (large round pins). While 2-pin European Type C plugs often fit Indian sockets, a dedicated Type D 3-pin universal adapter is strongly advised.");
            electronicsAdvice.add("Power Bank Regulations: Carry a 10,000mAh-20,000mAh power bank strictly in your HAND/CABIN luggage. Indian airport security will confiscate power banks found in checked baggage.");
            electronicsAdvice.add("Train Charging: On Indian Railways trains, 2-tier and 3-tier AC coaches provide 110V/220V charging sockets beside berths. Keep a 2-meter charging cable handy.");
        } else if (isJapanDest) {
            electronicsAdvice.add("Voltage: 100V, 50Hz (East) / 60Hz (West). US 2-prong Type A plugs fit directly, but sockets rarely have the 3rd grounding pin.");
            electronicsAdvice.add("Pocket Wi-Fi / eSIM: Reserve an Airalo/Ubigi eSIM or pick up a mobile pocket Wi-Fi router at Haneda/Narita airport.");
        } else {
            electronicsAdvice.add("Universal Travel Adapter: Ensure multi-country socket compatibility with built-in USB-C fast charging ports.");
            electronicsAdvice.add("Power Bank: Keep portable power banks under 100Wh (approx. 27,000mAh) within carry-on baggage.");
        }

        // 3. TRANSIT SPECIFIC PACKING HACKS
        List<String> transitHacks = new ArrayList<>();
        if (transport.contains("TRAIN")) {
            if (isIndiaDest) {
                transitHacks.add("IRCTC Berth Comfort: Even though clean bedsheets and blankets are supplied in AC coaches (1AC, 2AC, 3AC), carry a light personal travel shawl and disinfectant wipes for berth armrests.");
                transitHacks.add("Luggage Security: Carry a compact wire chain and padlock to secure large suitcases under lower berths on overnight trains.");
                transitHacks.add("Seat Survival Pouch: Keep a small sling pouch containing your passport/ID, phone, power bank, and wet wipes so your main bag stays stowed.");
            } else {
                transitHacks.add("Overnight Train Gear: Noise-cancelling earbuds and a memory-foam neck pillow guarantee sound sleep.");
                transitHacks.add("Overhead Rack Protocol: Pack your heaviest items at the bottom of your suitcase for stable lifting into overhead train racks.");
            }
        } else if (transport.contains("FLIGHT")) {
            transitHacks.add("Cabin Essentials Pouch: Pack 1 full spare change of clothes, vital medication, and chargers in your carry-on in case checked luggage is delayed.");
            transitHacks.add("TSA Liquid Rule: Liquid toiletries must be in containers 100ml (3.4oz) or smaller, contained within a single clear 1-quart zip bag.");
        } else {
            transitHacks.add("Road Trip Ergonomics: Keep offline Google Maps pre-downloaded and a multi-port 12V USB car charger plugged in.");
        }

        // 4. LOCAL SURVIVAL GEMS
        List<String> localGems = new ArrayList<>();
        if (isIndiaDest) {
            localGems.add("Drinking Water Golden Rule: Drink ONLY branded bottled water with unbroken seals (Bisleri, Kinley, Aquafina). Use bottled water for brushing teeth if you have a sensitive stomach.");
            localGems.add("Taxis & Autos: Always book via Uber, Ola, or Rapido apps. Avoid hailing unmetered autos directly outside railway stations without an app booking.");
            localGems.add("Payment Methods: Digital UPI (PhonePe/Google Pay) is used everywhere, but foreign cards aren't universally linked to UPI yet. Keep 2,000-5,000 rupees in physical cash (100, 200, 500 notes).");
        } else {
            localGems.add("Local Dining Rule: Skip tourist restaurants adjacent to monuments; venture 2-3 blocks into side avenues where local residents dine.");
            localGems.add("Emergency Currency: Stash an emergency $50 / 50 euro note inside your phone case or shoe insole.");
        }

        result.put("clothingAdvice", clothingAdvice);
        result.put("electronicsAdvice", electronicsAdvice);
        result.put("transitHacks", transitHacks);
        result.put("localGems", localGems);

        return result;
    }

    public String generateChatAnswer(Trip trip, String question) {
        if (question == null || question.trim().isEmpty()) {
            return "Hello! How can I assist you with your packing list, local cultural etiquette, transit standards, or destination tips?";
        }

        String q = question.toLowerCase();
        String dest = (trip != null && trip.getDestination() != null) ? trip.getDestination().toLowerCase() : "";
        boolean isIndia = dest.contains("india") || dest.contains("hyderabad") || dest.contains("mumbai") || dest.contains("delhi");

        // 1. INDIAN RAILWAYS / TRAIN TICKETS / STANDARDS / RAC / WL
        if (q.contains("train") || q.contains("rac") || q.contains("wl") || q.contains("tatkal") || q.contains("ticket") || q.contains("irctc") || q.contains("coach")) {
            return "Indian Railways (IRCTC) Standards & Guide for Travelers:\n\n" +
                    "1. Understanding Ticket Statuses:\n" +
                    "   - CNF (Confirmed): You have an assigned coach and berth (e.g., Coach B3, Berth 24).\n" +
                    "   - RAC (Reservation Against Cancellation): You have guaranteed entry and a seat on the train, but share a side-lower berth with another traveler until cancellations occur.\n" +
                    "   - WL (Waiting List, e.g., GNWL/RLWL): You cannot board a reserved AC coach if your ticket remains WL after the final chart is prepared (4 hours before departure). It gets automatically refunded if booked online.\n" +
                    "   - Tatkal Quota: Emergency last-minute tickets opening at 10:00 AM (AC classes) and 11:00 AM (Non-AC) 1 day before journey.\n\n" +
                    "2. Coach Classes Explained:\n" +
                    "   - 1AC: 2-berth or 4-berth private lockable compartments with full bedding.\n" +
                    "   - 2AC: 2-tier open curtain berths with clean washed bedsheets, pillow, and blanket provided.\n" +
                    "   - 3AC: 3-tier berths with bedding. Most popular, clean, air-conditioned.\n" +
                    "   - Sleeper (SL): Non-air-conditioned open windows with ceiling fans (can be dusty and crowded).\n\n" +
                    "3. What You Need Onboard:\n" +
                    "   - Original Government ID / Passport matching the passenger name on your e-Ticket.\n" +
                    "   - Charging points are available near berths (standard 220V/110V sockets).\n" +
                    "   - Food: You can order hot meals via the official IRCTC e-Catering app delivered to your train seat at major station stops!";
        }

        // 2. ELECTRONICS & POWER ADAPTERS
        if (q.contains("electronic") || q.contains("adapter") || q.contains("charger") || q.contains("voltage") || q.contains("plug") || q.contains("socket") || q.contains("power bank")) {
            if (isIndia) {
                return "Electronics & Voltage Guide for India (e.g., Hyderabad):\n\n" +
                        "1. Voltage Difference: India uses 230 Volts at 50 Hz.\n" +
                        "   - If your devices are from North America (120V), check the label on your charger brick. If it reads INPUT: 100-240V ~ 50/60Hz (like iPhones, MacBooks, iPad chargers), you do NOT need a voltage converter, only a plug adapter.\n" +
                        "   - High-draw heating appliances like hair straighteners and dryers that only say 120V will burn out unless you have a heavy-duty step-down voltage converter.\n\n" +
                        "2. Socket Types: India uses Type D (3 round pins) and Type M (larger round pins). Standard European 2-pin Type C plugs often fit in Indian sockets, but a 3-pin universal adapter provides a grounded connection.\n\n" +
                        "3. Power Banks: Power banks MUST be kept in your hand/carry-on baggage (max capacity 100Wh / 27,000mAh). Airport security across India will confiscate power banks in checked luggage.";
            } else {
                return "Electronics Guide for Your Journey:\n\n" +
                        "- Ensure all your chargers support dual-voltage (100-240V).\n" +
                        "- Pack a universal multi-socket travel adapter with USB-C ports.\n" +
                        "- Pack a 10,000mAh to 20,000mAh power bank strictly in your cabin luggage.\n" +
                        "- Keep a 2-meter braided cable for bedside sockets.";
            }
        }

        // 3. CLOTHING & DRESS ETIQUETTE
        if (q.contains("cloth") || q.contains("dress") || q.contains("wear") || q.contains("outfit") || q.contains("shoes") || q.contains("temple") || q.contains("modest")) {
            if (isIndia) {
                return "Clothing & Cultural Etiquette for India & Hyderabad:\n\n" +
                        "1. Everyday Fabrics: The climate is warm and tropical. Pack 100% breathable pure cotton, linen, or bamboo rayon. Light-colored tops reflect heat.\n\n" +
                        "2. Cultural Dress Standards:\n" +
                        "   - Modesty is deeply appreciated: Avoid tight tank tops, short shorts, or revealing cut-outs in public streets and heritage quarters.\n" +
                        "   - For men: Short-sleeved cotton shirts, polos, and linen/cotton trousers.\n" +
                        "   - For women: Loose cotton kurtas/tunics, linen shirts with ankle-length cotton pants or maxi skirts. Carry a lightweight scarf/dupatta in your daypack to cover head/shoulders at places of worship.\n\n" +
                        "3. Shoes: Pack slip-on loafers, sandals, or easily removable sneakers. Footwear is strictly prohibited inside religious sanctums, mosques, and traditional households.";
            } else {
                return "Smart Clothing Recommendation:\n\n" +
                        "- Pack versatile neutral layers (black, navy, white, olive, beige) so 4 tops match 3 bottoms.\n" +
                        "- Bring 1 comfortable pair of walking sneakers (12,000+ daily steps) and 1 pair of evening shoes.\n" +
                        "- Pack moisture-wicking undergarments and an ultra-light rain or wind layer.";
            }
        }

        // 4. WATER & HEALTH
        if (q.contains("water") || q.contains("food") || q.contains("stomach") || q.contains("health") || q.contains("drink") || q.contains("sick")) {
            if (isIndia) {
                return "Drinking Water & Food Safety in India:\n\n" +
                        "1. Never drink tap water: Always buy sealed bottled mineral water from reputable national brands: Bisleri, Kinley, or Aquafina. Look closely at the tamper-evident seal cap.\n\n" +
                        "2. Ice & Salads: Avoid drinks with crushed ice from roadside carts. Avoid raw unpeeled salads from roadside stalls; eat hot, freshly cooked food like piping hot Biryani, Dosas, and Naans!\n\n" +
                        "3. First-Aid Essentials: Pack electrolyte sachets (Electral / ORS), anti-diarrheal medication, and motion sickness pills.";
            } else {
                return "Water & Health Guidelines:\n\n" +
                        "- Check local tap water potability for your destination before consuming.\n" +
                        "- Stay hydrated throughout long flights and transit legs with electrolyte powders.\n" +
                        "- Carry a compact personal first-aid pouch with pain relief, band-aids, and indigestion tablets.";
            }
        }

        // 5. HYDERABAD SPECIFICS
        if (q.contains("hyderabad") || q.contains("biryani") || q.contains("charminar") || q.contains("golconda")) {
            return "Hyderabad Insider Advice & Etiquette:\n\n" +
                    "1. Historic Landmarks:\n" +
                    "   - Charminar & Laad Bazaar: Visit early morning (8:30-10:00 AM) to experience the monument before crowds arrive. Around Laad Bazaar, shop for authentic pearls and lacquer bangles.\n" +
                    "   - Golconda Fort: Attend the evening sound-and-light show. Wear sturdy walking shoes as the fort climb has uneven stone steps.\n\n" +
                    "2. Iconic Food Culture:\n" +
                    "   - Hyderabadi Dum Biryani: Head to Cafe Bahar (Hyderguda), Hotel Shadab (near High Court), or Paradise for authentic slow-cooked dum biryani.\n" +
                    "   - Irani Chai: Sit at Nimrah Cafe & Bakery facing Charminar and dip fresh sweet-salty Osmania biscuits into hot Irani chai.\n\n" +
                    "3. Local Transit:\n" +
                    "   - Use the Hyderabad Metro to bypass traffic jams between Hitec City and the central city.\n" +
                    "   - For shorter hops, book an auto-rickshaw via Uber Auto or Ola Auto for exact GPS meter fares.";
        }

        // 6. DEFAULT GENERAL HELPFUL RESPONSE
        return "Gemini Travel Advisor Recommendations for " + (dest.isEmpty() ? "your trip" : dest.toUpperCase()) + ":\n\n" +
                "You asked: \"" + question + "\"\n\n" +
                "- Packing Optimization: Use the Ranger-Roll bundling technique to compress shirts and pants into 40% less luggage volume.\n" +
                "- Digital Preparedness: Download Google Maps offline for your destination area before arriving so you never lose navigation without cell service.\n" +
                "- Documents: Email digital PDF copies of your passport photo page, visa, tickets, and travel insurance policy to yourself.\n" +
                "- Local Etiquette: When visiting heritage quarters, maintain respectful voices and observe photography guidelines posted at monument entrances.\n\n" +
                "Feel free to ask me any further questions about clothes, electronics, transit tickets, or dining!";
    }
}
