package com.example.SundayCCWeek8ChatWebApp;

import com.example.SundayCCWeek8ChatWebApp.model.Chat;
import com.example.SundayCCWeek8ChatWebApp.model.Message;
import com.example.SundayCCWeek8ChatWebApp.model.User;
import com.example.SundayCCWeek8ChatWebApp.repository.ChatRepository;
import com.example.SundayCCWeek8ChatWebApp.repository.MessageRepository;
import com.example.SundayCCWeek8ChatWebApp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@SpringBootApplication
public class SundayCcWeek8ChatWebAppApplication {
	private static final List<String> userNames = List.of(
			"BambooBrawler", "SleepySifu", "PawsOfFury", "ThePandarin", "KungFuPanda",
			"Bamboozled", "SirRollsAlot", "LordFluffenstuff", "GeneralTsoPanda", "ChubbyCheeks",
			"Pandamonium", "TheSnacktivist", "ZenMasterZzz", "RolliePollie", "BambooBandit",
			"FluffyButt", "MasterOogwayFan", "TheFuzzyWuzzy", "PandaExpressCEO", "BambooNinja",
			"SleepyBear", "CuddleCommander", "SnoreLord", "TheGreatRoll", "BambooBreath",
			"PandaPatrol", "SirEatsAlot", "DukeOfDozing", "TheAbominableSnowPanda", "CaptainCuddles"
	);

	private static final List<String> chatNames = List.of(
			"The Bamboo Lounge", "Sichuan Secrets", "The Zen Den", "Panda Playground",
			"FluffyButt Fanclub", "Snack Planning Committee", "Nap Time Strategies", "Rolling Hills",
			"The Fury Fields", "Black and White Banter", "Pawsitive Vibes Only", "The Great Wall of Fluff",
			"Kung Fu Practice", "Secret Noodle Recipes", "Bamboo Forest Hangout", "The Sleepy Circle",
			"Clan of the Chubby", "The Cuddle Puddle", "Dojo Discussions", "The Pandarin's Palace"
	);

	private static final List<String> messages = List.of(
			"Is it snack time yet?",
			"I'm feeling a bit bamboozled today.",
			"Just woke up from a 12-hour nap. What did I miss?",
			"You guys wanna go roll down a hill?",
			"My life is just black, white, and shades of green.",
			"I've got a PhD in Cuddling.",
			"Who ate the last of the bamboo shoots?!",
			"I'm not fat, I'm just fluffy.",
			"Thinking about my next meal. And the one after that.",
			"The key to life is to find your inner peace... and a comfy napping spot.",
			"I tried kung fu once. Pulled a muscle.",
			"Let's be honest, we're all just here for the snacks.",
			"I'm in a committed relationship with my bed.",
			"Do these black spots make me look chubby?",
			"Just saw a red panda. Such a show-off."
	);
	public static void main(String[] args) {
		SpringApplication.run(SundayCcWeek8ChatWebAppApplication.class, args);
	}

	@Bean
	CommandLineRunner populateDB(UserRepository userRepository, ChatRepository chatRepository, MessageRepository messageRepository) {
		return args -> {
			// safety check: only run if the database is empty
			if (userRepository.count() == 0) {
				List<User> createdUsers = populateUsers(userRepository);
				List<Chat> createdChats = populateChats(chatRepository);
				linkUsersToChats(chatRepository, createdUsers, createdChats);
				populateMessages(messageRepository, createdChats);

				System.out.println(">>> Finished populating database.");
			} else {
				System.out.println(">>> Database is not empty. Skipping data population.");
			}
		};
	}

	private List<User> populateUsers(UserRepository userRepository) {
		List<User> usersToCreate = new ArrayList<>();
		for (String name : userNames) {
			User user = new User();
			user.setName(name);
			user.setPassword("test");
			usersToCreate.add(user);
		}
		return userRepository.saveAll(usersToCreate);
	}

	private List<Chat> populateChats(ChatRepository chatRepository) {
		List<Chat> chatsToCreate = new ArrayList<>();
		for (String name : chatNames) {
			Chat chat = new Chat();
			chat.setName(name);
			chatsToCreate.add(chat);
		}
		return chatRepository.saveAll(chatsToCreate);
	}

	private void linkUsersToChats(ChatRepository chatRepository, List<User> allUsers, List<Chat> allChats) {
		for (Chat chat : allChats) {
			Collections.shuffle(allUsers);
			for (int i = 0; i < 10; i++) {
				User randomUser = allUsers.get(i);
				chat.getUsers().add(randomUser);
				randomUser.getChats().add(chat);
			}
		}
		chatRepository.saveAll(allChats);
	}

	private void populateMessages(MessageRepository messageRepository, List<Chat> allChats) {
		List<Message> messagesToCreate = new ArrayList<>();
		Random random = new Random();

		for (Chat chat : allChats) {
			List<User> members = chat.getUsers();
			if (members.isEmpty()) continue;

			for (int i = 0; i < 10; i++) {
				User randomUser = members.get(random.nextInt(members.size()));

				Message message = new Message();
				message.setChat(chat);
				message.setUser(randomUser);
				message.setContent(messages.get(random.nextInt(messages.size())));

				messagesToCreate.add(message);
			}
		}
		messageRepository.saveAll(messagesToCreate);
	}
}
