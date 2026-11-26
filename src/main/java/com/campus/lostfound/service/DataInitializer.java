package com.campus.lostfound.service;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.ItemRepository;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public DataInitializer(UserRepository userRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public void run(String... args) {
        // Seed Default Admin if not exists
        if (userRepository.findByEmailIgnoreCase("admin@campus.edu").isEmpty()) {
            User admin = new User(
                "admin@campus.edu",
                "Campus Authority Administrator",
                "ADMIN",
                UserService.hashPassword("Admin@123")
            );
            userRepository.save(admin);
            System.out.println(">>> Seeded default admin: admin@campus.edu / Admin@123");
        }

        // Seed Sample Found Items if empty
        if (itemRepository.count() == 0) {
            Item earbuds = new Item();
            earbuds.setName("Boat Airdopes Wireless Earbuds");
            earbuds.setColour("Matte Black");
            earbuds.setSize("Small");
            earbuds.setIsElectronic(true);
            earbuds.setCategory("Electronics");
            earbuds.setLocation("Central Library, 2nd Floor Study Table 14");
            earbuds.setBrand("Boat");
            earbuds.setDescription("Wireless earbuds in charging case found on the floor near table 14.");
            earbuds.setSecretClue("Yellow smiley sticker on the back of the case and small scratch on right bud.");
            earbuds.setReportType("FOUND");
            earbuds.setStatus("OPEN");
            earbuds.setReportedByEmail("librarian@campus.edu");
            earbuds.setEventDate(LocalDateTime.now().minusHours(4));
            itemRepository.save(earbuds);

            Item bottle = new Item();
            bottle.setName("Milton Stainless Steel Insulated Bottle");
            bottle.setColour("Silver Blue");
            bottle.setSize("Medium");
            bottle.setIsElectronic(false);
            bottle.setCategory("Bottles");
            bottle.setLocation("Student Food Court, Table 8");
            bottle.setBrand("Milton");
            bottle.setDescription("750ml insulated thermos water bottle left behind.");
            bottle.setSecretClue("Initials 'R.K.' engraved at the bottom rim.");
            bottle.setReportType("FOUND");
            bottle.setStatus("OPEN");
            bottle.setReportedByEmail("canteen.staff@campus.edu");
            bottle.setEventDate(LocalDateTime.now().minusHours(8));
            itemRepository.save(bottle);

            Item calc = new Item();
            calc.setName("Casio fx-991EX ClassWiz Calculator");
            calc.setColour("Black");
            calc.setSize("Small");
            calc.setIsElectronic(true);
            calc.setCategory("Electronics");
            calc.setLocation("Engineering Block B, Room 302");
            calc.setBrand("Casio");
            calc.setDescription("Scientific calculator left on the podium after Math lecture.");
            calc.setSecretClue("Folded periodic table chart tucked inside the sliding hard cover.");
            calc.setReportType("FOUND");
            calc.setStatus("OPEN");
            calc.setReportedByEmail("maths.ta@campus.edu");
            calc.setEventDate(LocalDateTime.now().minusDays(1));
            itemRepository.save(calc);

            Item bag = new Item();
            bag.setName("Wildcraft Outdoor Backpack");
            bag.setColour("Navy Blue");
            bag.setSize("Big");
            bag.setIsElectronic(false);
            bag.setCategory("Bags");
            bag.setLocation("Sports Complex Gymnasium Bleachers");
            bag.setBrand("Wildcraft");
            bag.setDescription("Dark navy backpack found on the top tier of bleachers.");
            bag.setSecretClue("Contains a grey badminton wristband and a chemistry lab manual.");
            bag.setReportType("FOUND");
            bag.setStatus("OPEN");
            bag.setReportedByEmail("coach.mike@campus.edu");
            bag.setEventDate(LocalDateTime.now().minusDays(2));
            itemRepository.save(bag);

            System.out.println(">>> Seeded 4 sample campus mystery items.");
        }
    }
}
