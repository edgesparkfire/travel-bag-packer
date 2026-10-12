package com.example.bag.service;

import com.example.bag.model.CityGuide;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CityGuideService {

    private final Map<String, CityGuide> cityDatabase = new HashMap<>();

    public CityGuideService() {
        seedCityDatabase();
    }

    public CityGuide getGuideForCity(String cityQuery) {
        if (cityQuery == null || cityQuery.trim().isEmpty()) {
            return getDefaultGuide("Your Destination");
        }

        String normalized = cityQuery.trim().toLowerCase();

        // 1. Check for Pakistan cities
        if (normalized.contains("pakistan")) {
            if (normalized.contains("hyderabad") && cityDatabase.containsKey("hyderabad_pakistan")) {
                return cityDatabase.get("hyderabad_pakistan");
            }
            if (normalized.contains("karachi") && cityDatabase.containsKey("karachi")) {
                return cityDatabase.get("karachi");
            }
            if (normalized.contains("lahore") && cityDatabase.containsKey("lahore")) {
                return cityDatabase.get("lahore");
            }
        }

        // 2. Check for Hyderabad specifically
        if (normalized.contains("hyderabad")) {
            if (normalized.contains("pakistan")) {
                return cityDatabase.get("hyderabad_pakistan");
            } else {
                return cityDatabase.get("hyderabad_india");
            }
        }

        // 3. Key search
        for (Map.Entry<String, CityGuide> entry : cityDatabase.entrySet()) {
            if (normalized.contains(entry.getKey()) || entry.getKey().contains(normalized)) {
                return entry.getValue();
            }
        }

        return getDefaultGuide(cityQuery);
    }

    private void seedCityDatabase() {
        // HYDERABAD, INDIA
        Map<String, String> hydEmergency = new LinkedHashMap<>();
        hydEmergency.put("Police Control Room", "100 / 112");
        hydEmergency.put("Ambulance (Emergency Medical)", "108");
        hydEmergency.put("Women Helpline (She Teams)", "181 / 1091");
        hydEmergency.put("Tourist Information Helpline", "1363");
        hydEmergency.put("Cyberabad Police WhatsApp", "+91 9490617444");

        Map<String, String> hydPhrases = new LinkedHashMap<>();
        hydPhrases.put("Hello / Greetings", "Namaskaram (Telugu) / Adaab (Urdu) / Namaste");
        hydPhrases.put("Thank you", "Dhanyavadalu (Telugu) / Shukriya (Urdu)");
        hydPhrases.put("How much is this?", "Idhi entha? (Telugu) / Yeh kitne ka hai? (Hindi)");
        hydPhrases.put("Where is the station / hospital?", "Station ekkada undi? (Telugu) / Station kahan hai?");
        hydPhrases.put("Less spicy please", "Karam thakkuva veyandi (Telugu) / Mirchi kam (Hindi)");
        hydPhrases.put("Do you speak English?", "Meeku English vachha? (Telugu) / English aati hai?");

        CityGuide hydIndia = CityGuide.builder()
                .city("Hyderabad")
                .country("India")
                .generalSummary("The historic City of Pearls and major tech metropolis in Telangana, renowned for Nizami heritage, majestic Golconda Fort, and world-famous Hyderabadi Biryani.")
                .knowledgeSource("Telangana State Tourism Development Corp (TSTDC) & Cyberabad Police Directorate")
                .bestTimeToVisit("October to March (pleasant 18C-28C; avoid peak summer April-May 42C)")
                .voltageAndSocket("230V, 50Hz. Socket types C, D, and M (3 round pins). Universal adapter recommended.")
                .emergencyContacts(hydEmergency)
                .transportAdvice(Arrays.asList(
                        "Hyderabad Metro Rail (Red, Blue, Green lines) is the fastest air-conditioned transit across Secunderabad, Ameerpet, Hitec City, and Jubilee Hills. Purchase a smart card or QR ticket via WhatsApp/Paytm.",
                        "Auto-rickshaws: Always book via Uber Auto, Ola Auto, or Rapido to avoid inflated off-meter haggling at railway stations and bazaars.",
                        "Railway Stations: For Secunderabad (SC) or Hyderabad Deccan (Nampally), arrive 40 minutes prior to departure. Keep your IRCTC m-ticket and original passport/ID ready for ticket examiner (TTE) check."
                ))
                .rideApps(Arrays.asList("Uber (Cabs & Autos)", "Ola Cabs", "Rapido (Bike Taxi & Auto-rickshaw)"))
                .culturalEtiquette(Arrays.asList(
                        "Modest Dress: Wear conservative attire covering shoulders and knees when visiting historic monuments and places of worship (Mecca Masjid, Charminar, Birla Mandir).",
                        "Footwear Rule: Always remove shoes before stepping into temples, mosques, and traditional Indian homes. Slip-on shoes are easiest.",
                        "Dining & Hand Protocol: Use your right hand for eating, passing food, and handing cash or cards. Avoid using the left hand for interpersonal exchanges.",
                        "Bargaining Etiquette: In Laad Bazaar beside Charminar, respectful bargaining for bangles and pearls is customary (start at 60-70% of quoted price)."
                ))
                .tapWaterStatus("Strictly drink sealed bottled mineral water (trusted brands: Bisleri, Kinley, Aquafina with unbroken tamper seals). Avoid raw tap water and street iced drinks.")
                .tippingCulture("7% to 10% is customary at sit-down restaurants unless a service charge is already printed on the bill. Rounding off auto-rickshaw fares by 20-30 rupees is standard.")
                .commonPhrases(hydPhrases)
                .mustTryDishes(Arrays.asList(
                        "Authentic Hyderabadi Dum Biryani (Paradise, Hotel Shadab, or Cafe Bahar)",
                        "Irani Chai with crisp Osmania Biscuits at Nimrah Cafe overlooking Charminar",
                        "Hyderabadi Haleem (slow-cooked wheat, meat, lentils & spices)",
                        "Mirchi ka Salan & Double ka Meetha (royal saffron bread pudding)"
                ))
                .build();
        cityDatabase.put("hyderabad_india", hydIndia);
        cityDatabase.put("hyderabad", hydIndia);

        // HYDERABAD, SINDH, PAKISTAN
        Map<String, String> hydPakEmergency = new LinkedHashMap<>();
        hydPakEmergency.put("Police Emergency (Madadgar)", "15");
        hydPakEmergency.put("Edhi Ambulance Service", "115");
        hydPakEmergency.put("Rescue 1122 (Medical & Fire)", "1122");
        hydPakEmergency.put("Chhipa Emergency Ambulance", "1020");
        hydPakEmergency.put("Tourist Police / STDC Info", "+92 21 99206337");

        Map<String, String> hydPakPhrases = new LinkedHashMap<>();
        hydPakPhrases.put("Hello / Greetings", "As-salamu alaykum (Urdu) / Kiyan ahyo? (Sindhi)");
        hydPakPhrases.put("Thank you", "Shukriya (Urdu) / Meherbani (Sindhi)");
        hydPakPhrases.put("How much is this?", "Yeh kitne ka hai? (Urdu) / Heh ketre jo aahe? (Sindhi)");
        hydPakPhrases.put("Where is the station?", "Station kahan hai? (Urdu) / Station kithey aahe? (Sindhi)");
        hydPakPhrases.put("Less spicy please", "Mirchi kam rakhein (Urdu)");
        hydPakPhrases.put("Do you speak English?", "Aap English boltay hain?");

        CityGuide hydPak = CityGuide.builder()
                .city("Hyderabad")
                .country("Pakistan")
                .generalSummary("The historic second-largest city in Sindh, Pakistan, historic capital of the Kalhora and Talpur dynasties, famed for Pacco Qillo (Pakka Qila), the Sindh Museum, hand-crafted lacquerware (Jandi), and Resham Gali bazaars.")
                .knowledgeSource("Sindh Tourism Development Corporation (STDC) & Government of Sindh")
                .bestTimeToVisit("November to February (pleasant dry winter 14C-26C; avoid severe summer April-June above 42C)")
                .voltageAndSocket("230V, 50Hz. Socket types C and D (round 2-pin and 3-pin). Universal adapter recommended.")
                .emergencyContacts(hydPakEmergency)
                .transportAdvice(Arrays.asList(
                        "Auto-rickshaws are the primary city transit across Latifabad, Qasimabad, and the old city center. Indrive and Careem provide digital fixed pricing.",
                        "Hyderabad Junction (HDR) is a major Pakistan Railways stop on the main line connecting to Karachi (2 hours via Tezgam, Green Line, Karakoram Express) and Lahore.",
                        "M-9 Karachi-Hyderabad Motorway: Reliable 2.5-hour AC bus coaches (Daewoo Express, Faisal Movers) connect Hyderabad directly with Karachi Jinnah International Airport (KHI)."
                ))
                .rideApps(Arrays.asList("Careem", "Indrive", "Yango", "Uber"))
                .culturalEtiquette(Arrays.asList(
                        "Modest Dress: Wear conservative clothing covering shoulders and legs (Shalwar Kameez or loose shirts and trousers) across bazaars, heritage landmarks, and religious shrines.",
                        "Shrine & Mosque Etiquette: Always remove shoes before entering shrines (e.g. Shrine of Shah Abdul Latif Bhittai in nearby Bhit Shah) or mosques. Women should carry a dupatta/scarf to cover hair.",
                        "Hospitality & Dining: Sindhi hospitality is warm. Gratefully accept offered chai (tea). Always eat and exchange money with the right hand.",
                        "Photography: Always ask permission before photographing people in traditional bazaars or religious shrines."
                ))
                .tapWaterStatus("Strictly drink sealed bottled mineral water (Nestle Pure Life, Aquafina) with unbroken tamper seal. Avoid raw tap water and street crushed-ice drinks.")
                .tippingCulture("5% to 10% is customary at sit-down family restaurants. Rounding up rickshaw fares by 30-50 PKR is appreciated.")
                .commonPhrases(hydPakPhrases)
                .mustTryDishes(Arrays.asList(
                        "Sindhi Biryani (fragrant spiced rice with potatoes and marinated meat)",
                        "Bombay Bakery Famous Coffee Cake & Macaroons (iconic Hyderabad landmark bakery since 1911)",
                        "Rabri & Kulfi Falooda from Shahi Bazaar",
                        "Sindhi Sai Bhaji & Koki"
                ))
                .build();
        cityDatabase.put("hyderabad_pakistan", hydPak);

        // KARACHI, PAKISTAN
        CityGuide karachi = CityGuide.builder()
                .city("Karachi")
                .country("Pakistan")
                .generalSummary("Pakistan's premier coastal metropolis and economic powerhouse on the Arabian Sea, famed for vibrant beaches, Port Grand, Burns Road food street, and Mohatta Palace.")
                .knowledgeSource("Sindh Tourism & Karachi Tourism Authority")
                .bestTimeToVisit("November to February (pleasant coastal weather 18C-28C)")
                .voltageAndSocket("230V, 50Hz. Socket types C and D.")
                .emergencyContacts(hydPakEmergency)
                .transportAdvice(Arrays.asList(
                        "Use Careem, Indrive, or Uber for safe, air-conditioned point-to-point transit across Clifton, DHA, and Saddar.",
                        "Red Line Peoples Bus Service provides modern AC public transport across major city arteries.",
                        "Jinnah International Airport (KHI) is approximately 30-45 minutes from downtown via Shahrah-e-Faisal."
                ))
                .rideApps(Arrays.asList("Careem", "Indrive", "Uber", "Yango"))
                .culturalEtiquette(Arrays.asList(
                        "Cosmopolitan city with modest clothing recommended for markets, mosques, and heritage quarters.",
                        "Burns Road is the world-renowned center for Karachi street cuisine; late-night dining until 2 AM is customary."
                ))
                .tapWaterStatus("Drink only sealed bottled mineral water.")
                .tippingCulture("5-10% at sit-down restaurants.")
                .commonPhrases(hydPakPhrases)
                .mustTryDishes(Arrays.asList("Burns Road Nihari & Haleem", "Karachi Biryani", "Behari Kabab", "Gola Ganda shaved ice"))
                .build();
        cityDatabase.put("karachi", karachi);

        // LAHORE, PAKISTAN
        CityGuide lahore = CityGuide.builder()
                .city("Lahore")
                .country("Pakistan")
                .generalSummary("The cultural heart of Pakistan, famed for monumental Mughal architecture (Badshahi Mosque, Lahore Fort, Shalimar Gardens) and historic food streets.")
                .knowledgeSource("Walled City of Lahore Authority (WCLA) & Punjab Tourism")
                .bestTimeToVisit("November to February (cool winter days 12C-22C)")
                .voltageAndSocket("230V, 50Hz. Type C and D.")
                .emergencyContacts(hydPakEmergency)
                .transportAdvice(Arrays.asList(
                        "Lahore Metro (Orange Line) is Pakistan's first automated rapid transit system.",
                        "Metrobus runs dedicated north-south corridors connecting Shahdara to Gajjumata.",
                        "Ride-hailing via Careem, Indrive, or Uber is widely available."
                ))
                .rideApps(Arrays.asList("Careem", "Indrive", "Uber", "Yango"))
                .culturalEtiquette(Arrays.asList(
                        "Dress modestly covering shoulders and knees; women should carry scarves for Badshahi Mosque and shrines.",
                        "Remove footwear before stepping onto mosque and shrine courtyards."
                ))
                .tapWaterStatus("Drink only sealed bottled mineral water.")
                .tippingCulture("5-10% is standard in restaurants.")
                .commonPhrases(hydPakPhrases)
                .mustTryDishes(Arrays.asList("Lahori Chargha", "Butt Karahi at Lakshmi Chowk", "Halwa Puri breakfast", "Falooda from Anarkali"))
                .build();
        cityDatabase.put("lahore", lahore);

        // TOKYO, JAPAN
        Map<String, String> tokyoEmergency = new LinkedHashMap<>();
        tokyoEmergency.put("Ambulance / Fire", "119");
        tokyoEmergency.put("Police", "110");
        tokyoEmergency.put("Japan Visitor Helpline", "050-3816-2788");

        Map<String, String> tokyoPhrases = new LinkedHashMap<>();
        tokyoPhrases.put("Hello", "Konnichiwa");
        tokyoPhrases.put("Thank you", "Arigatou gozaimasu");
        tokyoPhrases.put("Excuse me / Sorry", "Sumimasen");
        tokyoPhrases.put("Where is the station?", "Eki wa doko desu ka?");

        cityDatabase.put("tokyo", CityGuide.builder()
                .city("Tokyo")
                .country("Japan")
                .generalSummary("A seamless blend of ultra-modern technology and timeless traditions. Extremely clean, punctual, and safe.")
                .knowledgeSource("Japan National Tourism Organization (JNTO) & Tokyo Metropolitan Gov")
                .bestTimeToVisit("March-May (Cherry Blossoms) or September-November (Autumn Foliage)")
                .voltageAndSocket("100V, 50/60Hz. Type A and B plugs (two flat pins).")
                .emergencyContacts(tokyoEmergency)
                .transportAdvice(Arrays.asList(
                        "Add a digital Suica or Pasmo transit IC card directly to Apple/Google Wallet for all trains, subways, and convenience stores.",
                        "Trains stop operating around midnight; plan transit or budget for late-night taxis.",
                        "Download 'Japan Travel by NAVITIME' for train routing and platform numbers."
                ))
                .rideApps(Arrays.asList("GO (Japan's #1 taxi app)", "Uber", "DiDi"))
                .culturalEtiquette(Arrays.asList(
                        "Keep noise minimal on public trains; phone calls are prohibited inside carriages.",
                        "Stand on the left side of escalators in Tokyo (right side in Osaka).",
                        "Strictly NO tipping. Leaving extra money can be perceived as an accidental overpayment."
                ))
                .tapWaterStatus("Safe to drink: Tokyo tap water is completely potable, clean, and tested daily.")
                .tippingCulture("Zero tipping. Exceptional service is included by default.")
                .commonPhrases(tokyoPhrases)
                .mustTryDishes(Arrays.asList(
                        "Authentic Tonkotsu Ramen",
                        "Fresh Nigiri Sushi at Tsukiji Outer Market",
                        "Crisp Tempura & Matcha Soft Serve"
                ))
                .build());

        // LONDON, UK
        Map<String, String> londonEmergency = new LinkedHashMap<>();
        londonEmergency.put("Emergency Services", "999 / 112");
        londonEmergency.put("Non-emergency Police", "101");

        Map<String, String> londonPhrases = new LinkedHashMap<>();
        londonPhrases.put("Hello", "Hello / Cheers");
        londonPhrases.put("Thank you", "Cheers / Thank you very much");
        londonPhrases.put("Excuse me", "Pardon me");

        cityDatabase.put("london", CityGuide.builder()
                .city("London")
                .country("United Kingdom")
                .generalSummary("A global capital of culture, history, world-renowned museums, and iconic royal landmarks.")
                .knowledgeSource("VisitBritain & Transport for London (TfL)")
                .bestTimeToVisit("May to September (longer daylight and mild temperatures)")
                .voltageAndSocket("230V, 50Hz. Type G plug (three rectangular pins).")
                .emergencyContacts(londonEmergency)
                .transportAdvice(Arrays.asList(
                        "Simply tap contactless debit/credit card or phone at Tube turnstiles; no paper ticket needed.",
                        "Stand on the RIGHT on all Underground escalators; let walking commuters pass on the left.",
                        "Black Cabs accept cards; minicabs must be pre-booked via apps."
                ))
                .rideApps(Arrays.asList("Uber", "Bolt", "Free Now (Black Cabs)"))
                .culturalEtiquette(Arrays.asList(
                        "Queuing (lining up) is strictly respected; never cut in line.",
                        "Major national museums (British Museum, Tate Modern) are free admission."
                ))
                .tapWaterStatus("Safe to drink: Tap water is fully potable across London.")
                .tippingCulture("10-12.5% in sit-down restaurants (often included as optional Service Charge).")
                .commonPhrases(londonPhrases)
                .mustTryDishes(Arrays.asList("Fish & Chips with mushy peas", "Sunday Roast with Yorkshire pudding", "Afternoon Cream Tea"))
                .build());

        // PARIS, FRANCE
        Map<String, String> parisEmergency = new LinkedHashMap<>();
        parisEmergency.put("European Emergency", "112");
        parisEmergency.put("SAMU Ambulance", "15");
        parisEmergency.put("Police", "17");

        Map<String, String> parisPhrases = new LinkedHashMap<>();
        parisPhrases.put("Hello", "Bonjour");
        parisPhrases.put("Thank you", "Merci beaucoup");
        parisPhrases.put("Please", "S'il vous plait");

        cityDatabase.put("paris", CityGuide.builder()
                .city("Paris")
                .country("France")
                .generalSummary("The City of Light, celebrated for art, fashion, gastronomy, and historic boulevards.")
                .knowledgeSource("Atout France (National Tourism Agency) & RATP Paris Transit")
                .bestTimeToVisit("April-June or September-October (mild weather and fewer crowds)")
                .voltageAndSocket("230V, 50Hz. Type C and E plugs (two round pins).")
                .emergencyContacts(parisEmergency)
                .transportAdvice(Arrays.asList(
                        "Paris Metro and RER connect all monuments. Buy Navigo Easy card or digital tickets on phone.",
                        "Validate your ticket before entering the train and keep it until you exit the station.",
                        "Watch out for pickpockets at crowded tourist hubs (Eiffel Tower, Louvre, Montmartre)."
                ))
                .rideApps(Arrays.asList("Uber", "Bolt", "G7 Taxi"))
                .culturalEtiquette(Arrays.asList(
                        "Always say 'Bonjour Madame/Monsieur' upon entering any boutique, cafe, or shop before asking questions.",
                        "Speak in moderate tones in cafes and public transport."
                ))
                .tapWaterStatus("Safe to drink: Parisian tap water is high quality. Order 'une carafe d'eau' for free tap water in restaurants.")
                .tippingCulture("Service is included by law. Leaving 1-2 euros extra for good service is customary.")
                .commonPhrases(parisPhrases)
                .mustTryDishes(Arrays.asList("Fresh Butter Croissants & Baguettes", "Duck Confit", "Steak Frites", "Macarons from Laduree"))
                .build());

        // NEW YORK, USA
        Map<String, String> nycEmergency = new LinkedHashMap<>();
        nycEmergency.put("Emergency Services", "911");
        nycEmergency.put("City Services & Info", "311");

        Map<String, String> nycPhrases = new LinkedHashMap<>();
        nycPhrases.put("Hello", "Hi / How are you doing?");
        nycPhrases.put("Thank you", "Thanks a lot");
        nycPhrases.put("Where is the subway?", "Where's the nearest subway station?");

        cityDatabase.put("new york", CityGuide.builder()
                .city("New York City")
                .country("United States")
                .generalSummary("The dynamic city that never sleeps, famous for Broadway, Central Park, skyscrapers, and vibrant distinct boroughs.")
                .knowledgeSource("NYC Tourism + Conventions & MTA Transit")
                .bestTimeToVisit("April to June or September to November (mild weather)")
                .voltageAndSocket("120V, 60Hz. Plug types A and B (two flat pins).")
                .emergencyContacts(nycEmergency)
                .transportAdvice(Arrays.asList(
                        "OMNY contactless pay-per-ride: Simply tap debit/credit card or Apple/Google Pay at subway turnstiles.",
                        "NYC Subway runs 24 hours a day, 7 days a week.",
                        "Yellow Taxis can be hailed on streets or booked via the Curb app."
                ))
                .rideApps(Arrays.asList("Uber", "Lyft", "Curb (Yellow Cabs)"))
                .culturalEtiquette(Arrays.asList(
                        "Walk on the right side of sidewalks; step to the side if checking your phone to let fast-walking locals pass.",
                        "Stand on the right on subway escalators."
                ))
                .tapWaterStatus("Safe to drink: NYC tap water from the Catskill watershed is world-renowned for exceptional quality.")
                .tippingCulture("18% to 22% is standard in sit-down dining and taxis.")
                .commonPhrases(nycPhrases)
                .mustTryDishes(Arrays.asList("Classic NY Style Pizza Slice", "Pastrami on Rye at Katz's Delicatessen", "NY Bagel with lox and schmear"))
                .build());
    }

    private CityGuide getDefaultGuide(String destination) {
        Map<String, String> emergency = new LinkedHashMap<>();
        emergency.put("General Emergency", "112 / 911");
        emergency.put("Tourist Police / Embassy Info", "Consult local embassy helpline");

        Map<String, String> phrases = new LinkedHashMap<>();
        phrases.put("Hello", "Hello");
        phrases.put("Thank you", "Thank you");
        phrases.put("Please", "Please");
        phrases.put("Where is the hospital?", "Where is the nearest medical center?");

        return CityGuide.builder()
                .city(destination)
                .country("Global Destination")
                .generalSummary("International travel intelligence for " + destination + ". Verified packing, transport, and cultural advisory.")
                .knowledgeSource("World Tourism Organization & International Travel Consular Network")
                .bestTimeToVisit("Spring and Autumn months for optimal weather and sightseeing conditions.")
                .voltageAndSocket("Check local standards (220-240V or 110-120V). Universal adapter recommended.")
                .emergencyContacts(emergency)
                .transportAdvice(Arrays.asList(
                        "Use licensed public transportation, official airport taxis, or app-based ride-hailing services.",
                        "Keep digital backups of passport, visa, and insurance stored offline."
                ))
                .rideApps(Arrays.asList("Uber", "Bolt", "Local Taxi Services"))
                .culturalEtiquette(Arrays.asList(
                        "Respect local customs and religious practices.",
                        "Keep emergency contacts and hotel address written down."
                ))
                .tapWaterStatus("Drink sealed bottled water when uncertain of local municipal infrastructure.")
                .tippingCulture("10% standard unless service charge is included.")
                .commonPhrases(phrases)
                .mustTryDishes(Arrays.asList("Local regional specialties", "Traditional neighborhood cuisine"))
                .build();
    }
}
