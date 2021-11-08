package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.view.ConsoleService;

import java.util.ArrayList;
import java.util.List;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };

    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private AccountService accountService;
    private UserService userService;
    private TransactionService transactionService;

    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out),
				new AuthenticationService(API_BASE_URL),
				new AccountService(),
				new UserService(),
				new TransactionService());
    	app.run();
    }

    public App(ConsoleService console,
			   AuthenticationService authenticationService,
			   AccountService accountService,
			   UserService userService,
			   TransactionService transactionService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.accountService = accountService;
		this.userService = userService;
		this.transactionService = transactionService;
	}

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}


	//option 1 - View your current balance
	private void viewCurrentBalance() {
    	Long currentUserId = currentUser.getUser().getId().longValue();
		Account account = accountService.getAccount(currentUserId);
		System.out.println("Account Balance is: " + account.getBalance());
		
	}


	// option 3 - View your past transfers
	private void viewTransferHistory() {
		Long userId = currentUser.getUser().getId().longValue();
		Transaction[] transactionArray = transactionService.getAllTransactions(userId);

		Long[] transactionIdArray = new Long[transactionArray.length];
		for(int i = 0; i < transactionArray.length; i++) {
			transactionIdArray[i] = transactionArray[i].getTransactionId();
		}
		Long selectedTransactionId = (Long)console.getChoiceFromOptions(transactionIdArray);

		Transaction selectedTransaction = null;
		for(int i = 0; i < transactionArray.length; i++){
			Long eachTransactionId = transactionArray[i].getTransactionId();
			if(selectedTransactionId.equals(eachTransactionId)){
				selectedTransaction = transactionArray[i];
			}
		}
		if(selectedTransaction != null) {
			System.out.println("Transfer ID           : " + selectedTransaction.getTransactionId());
			System.out.println("Transfer Type         : " + selectedTransaction.getTransactionType());
			System.out.println("Transfer Account From : " + selectedTransaction.getFromAccountId());
			System.out.println("Transfer Account To   : " + selectedTransaction.getToAccountId());
			System.out.println("Transfer Amount       : " + selectedTransaction.getAmount());
			System.out.println("Transfer Status       : " + selectedTransaction.getTransactionStatus());
		}
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}


	//option 2 - Send TE bucks
	private void sendBucks() {
    	//get all users from user service
		User[] users = userService.getUserList();
		if(users != null){
			boolean sendBucks = true;
			while (sendBucks) {
				// Filtering users other than the logged in user (Other Users)
				ArrayList<String> otherUsers = new ArrayList<>();
				// logged in user is a current user
				User loggedInUser = currentUser.getUser();
				// getting current user name
				String currentUsername = loggedInUser.getUsername();
				//iterating through the users array to find the other user except logged in user
				for(int i = 0; i < users.length; i++) {
					String username = users[i].getUsername();
					//if username is not equal to current user add in the names in otherUsers List(create otherUser List)
					if(!username.equals(currentUsername)){
						otherUsers.add(username);
					}
				}
				//prompt user to select registered user to send TE bucks
				System.out.println("Please select the user to send TE bucks");
				//using the helper method, holding the user input
				String toUsername = (String)console.getChoiceFromOptions(otherUsers.toArray());

				// Get hold of the toUser object based on toUsername
				User toUser = null;
				//iterating through the array to find the selected user to send the TE bucks
				for (int i = 0; i < users.length; i ++) {
					String username = users[i].getUsername();
					if(username.equals(toUsername)) {
						toUser = users[i];
					}
				}
				//prompt user to enter the amount
				System.out.println("Please enter amount to transfer!");
				String amountToTransfer = console.getUserInput(">>");

				Transaction transaction = new Transaction();
				transaction.setFromUserId(loggedInUser.getId().longValue());
				transaction.setToUserId(toUser.getId().longValue());
				transaction.setAmount(Double.parseDouble(amountToTransfer));
				transaction.setTransactionType("Send");
				transaction.setTransactionTypeId(2L);
				String transactionStatus = transactionService.sendTEBucks(transaction);
				System.out.println(transactionStatus);
				sendBucks = false;


			}
		} else {
			System.out.println("No user found");

		}

		
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}

}
