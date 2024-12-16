package carShopCompany;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import javafx.scene.chart.*;
import java.time.LocalTime;
import java.sql.Timestamp;
import java.time.LocalDate;
public class Dashboard extends BorderPane {
    // Refined color scheme
    private static final String PRIMARY_RED = "#E31837";     // Brighter, more modern red
    private static final String DARKER_RED = "#B22222";      // For hover states
    private static final String BG_BLACK = "#1A1A1A";        // Slightly darker for better contrast
    private static final String SECONDARY_BLACK = "#242424";  // For components
    private static final String ACCENT_GREY = "#333333";     // For hover states
    private static final String LIGHT_GREY = "#E5E5E5";      // For text
    private static final String SUCCESS_GREEN = "#4CAF50";   // For positive stats
    private static final String WARNING_ORANGE = "#FF9800";  // For alerts

    private final ImageView logoView;
    private Label currentTimeLabel;
    private Timeline clockTimeline;

    public Dashboard() {
        // Load logo image (you'll need to add your logo file to resources)
        logoView = new ImageView(new Image(getClass().getResourceAsStream("/luxuryCar.jpg")));
        logoView.setFitHeight(40);
        logoView.setFitWidth(40);
        logoView.setPreserveRatio(true);
        setMinSize(1000, 700);
        setPrefSize(1200, 800);

        setStyle("-fx-background-color: " + BG_BLACK + ";");
        
        VBox sidebar = createSidebar();
        setLeft(sidebar);
        
        VBox header = createHeader();
        setTop(header);
        
        // Initial content
        setCenter(createWelcomeContent());

        // Start clock update
        startClock();
    }

 // Add these fields at the top of your Dashboard class
    

    // Modify the createTimeDisplay method
    private HBox createTimeDisplay() {
        HBox timeDisplay = new HBox(5);
        timeDisplay.setAlignment(Pos.CENTER);

        currentTimeLabel = new Label(); // Initialize the label
        currentTimeLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 14px;"
        );
        
        updateTime(); // Initial update

        timeDisplay.getChildren().add(currentTimeLabel);
        
        // Start the clock after the label is initialized
        startClock();
        
        return timeDisplay;
    }

    private void startClock() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
        
        clockTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> updateTime()),
            new KeyFrame(Duration.seconds(1))
        );
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void updateTime() {
        if (currentTimeLabel != null) {
            currentTimeLabel.setText(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm:ss")
            ));
        }
    }

    // Add this method to clean up resources when closing
    public void cleanup() {
        if (clockTimeline != null) {
            clockTimeline.stop();
        }
    }
    private Node createWelcomeContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + BG_BLACK + ";" +
            "-fx-background-color: " + BG_BLACK + ";" +
            "-fx-padding: 0;"
        );

        VBox welcomeContent = new VBox(30);
        welcomeContent.setPadding(new Insets(30));

        // Welcome Section
        HBox welcomeSection = createWelcomeSection();
        
        // Statistics Cards
        HBox statsContainer = createStatisticsSection();
        
        // Charts Section
        HBox chartsSection = createChartsSection();
        
        // Recent Activity and Alerts Section
        HBox bottomSection = createBottomSection();

        welcomeContent.getChildren().addAll(
            welcomeSection,
            statsContainer,
            chartsSection,
            bottomSection
        );

        scrollPane.setContent(welcomeContent);
        return scrollPane;
    }

    private VBox createHeader() {
        VBox headerContainer = new VBox();
        headerContainer.setStyle("-fx-background-color: " + SECONDARY_BLACK + ";");

        // Main header with search and actions
        HBox header = new HBox(20);
        header.setPadding(new Insets(15, 30, 15, 30));
        header.setAlignment(Pos.CENTER_LEFT);

        // Page title with breadcrumb
        VBox titleBox = new VBox(5);
        Label pageTitle = new Label("Dashboard");
        pageTitle.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: white;"
        );
        
        Label breadcrumb = new Label("Home > Dashboard");
        breadcrumb.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + LIGHT_GREY + ";"
        );
        titleBox.getChildren().addAll(pageTitle, breadcrumb);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Right side elements
        HBox rightElements = new HBox(15);
        rightElements.setAlignment(Pos.CENTER);

        // Search Box
        TextField searchBox = new TextField();
        searchBox.setPromptText("Search...");
        searchBox.setPrefWidth(250);
        searchBox.setStyle(
            "-fx-background-color: " + ACCENT_GREY + ";" +
            "-fx-text-fill: white;" +
            "-fx-prompt-text-fill: " + LIGHT_GREY + ";" +
            "-fx-padding: 8 15;" +
            "-fx-background-radius: 20;" +
            "-fx-border-width: 0;"
        );

        // Notification Icon with count
        StackPane notificationPane = createNotificationIcon("🔔", "3");

        // User Profile
        HBox userProfile = new HBox(10);
        userProfile.setAlignment(Pos.CENTER);
        
        Label userName = new Label("Tala Faraj");
        userName.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );

        Circle userAvatar = new Circle(18);
        userAvatar.setFill(Color.valueOf(PRIMARY_RED));
        Label userInitial = new Label("T");
        userInitial.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );
        StackPane avatarPane = new StackPane(userAvatar, userInitial);

        userProfile.getChildren().addAll(userName, avatarPane);

        rightElements.getChildren().addAll(searchBox, notificationPane, userProfile);
        header.getChildren().addAll(titleBox, spacer, rightElements);

        // Add a subtle bottom border
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + PRIMARY_RED + ";");

        headerContainer.getChildren().addAll(header, separator);
        return headerContainer;
    }

    private StackPane createNotificationIcon(String icon, String count) {
        StackPane notificationStack = new StackPane();
        
        // Icon button
        Button notificationBtn = new Button(icon);
        notificationBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 18px;" +
            "-fx-cursor: hand;"
        );
        
        // Notification count
        Label countLabel = new Label(count);
        countLabel.setStyle(
            "-fx-background-color: " + PRIMARY_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-padding: 2 6;" +
            "-fx-background-radius: 10;" +
            "-fx-font-size: 10px;"
        );
        
        StackPane.setAlignment(countLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(countLabel, new Insets(-5, -5, 0, 0));
        
        notificationStack.getChildren().addAll(notificationBtn, countLabel);
        return notificationStack;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(280);
        sidebar.setStyle(
            "-fx-background-color: " + SECONDARY_BLACK + ";" +
            "-fx-border-width: 0 1 0 0;" +
            "-fx-border-color: " + PRIMARY_RED + ";"
        );

        // Logo and title
        HBox logoContainer = new HBox(15);
        logoContainer.setPadding(new Insets(0, 0, 20, 0));
        logoContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Car Shop Management");
        titleLabel.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-family: 'Segoe UI';"
        );

        logoContainer.getChildren().add(titleLabel);
        
     // Navigation buttons
        Button dashboardBtn = createNavButton("Dashboard", "🏠", true);
        Button carsBtn = createNavButton("Cars", "🚗", false);
        Button customersBtn = createNavButton("Customers", "👥", false);
        Button employeesBtn = createNavButton("Employees", "👤", false);
        Button ordersBtn = createNavButton("Orders", "📦", false);
        Button servicesBtn = createNavButton("Services", "🔧", false);
        Button reportsBtn = createNavButton("Reports", "📊", false);

        // Main Navigation
        VBox navigation = new VBox(8);  // Reduced spacing between buttons
        navigation.setPadding(new Insets(20, 0, 20, 0));

        navigation.getChildren().addAll(
            createNavButton("Dashboard", "🏠", true),
            createNavButton("Cars", "🚗"),
            createNavButton("Customers", "👥"),
            createNavButton("Employees", "👤"),
            createNavButton("Orders", "📦"),
            createNavButton("Services", "🔧"),
            createNavButton("Reports", "📊")
        );

        // Add button actions
        dashboardBtn.setOnAction(e -> {
            resetNavButtonsState();
            dashboardBtn.setStyle(getActiveButtonStyle());
            handleNavigation("Dashboard");
        });

        carsBtn.setOnAction(e -> {
            resetNavButtonsState();
            carsBtn.setStyle(getActiveButtonStyle());
            handleNavigation("Cars");
        });
        
        // Spacer and Logout
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-width: 1;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;"
        );

        // Hover effect for logout button
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(
            "-fx-background-color: " + PRIMARY_RED + ";" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-width: 1;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;"
        ));

        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-width: 1;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 10 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 5;" +
            "-fx-border-radius: 5;"
        ));

        // Add all components to sidebar
        sidebar.getChildren().addAll(
                logoContainer,
                new Separator(),
                dashboardBtn,
                carsBtn,
                customersBtn,
                employeesBtn,
                ordersBtn,
                servicesBtn,
                reportsBtn,
                spacer,
                logoutBtn
            );
        return sidebar;
    }
    
    private void resetNavButtonsState() {
        VBox sidebar = (VBox) getLeft();
        if (sidebar != null) {
            for (Node node : sidebar.getChildren()) {
                if (node instanceof Button) {
                    node.setStyle(getDefaultButtonStyle());
                }
            }
        }
    }

    private String getActiveButtonStyle() {
        return "-fx-background-color: " + PRIMARY_RED + ";" +
               "-fx-text-fill: white;" +
               "-fx-font-size: 14px;" +
               "-fx-padding: 0 20px;" +
               "-fx-background-radius: 5;" +
               "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 0);";
    }

    private String getDefaultButtonStyle() {
        return "-fx-background-color: transparent;" +
               "-fx-border-color: " + PRIMARY_RED + ";" +
               "-fx-border-width: 1px;" +
               "-fx-text-fill: " + PRIMARY_RED + ";" +
               "-fx-font-size: 14px;" +
               "-fx-padding: 0 20px;" +
               "-fx-background-radius: 5;" +
               "-fx-border-radius: 5;";
    }

    private Button createNavButton(String text, String icon, boolean isActive) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setPrefHeight(45);
        
        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 20px;");
        
        Label textLabel = new Label(text);
        textLabel.setStyle("-fx-font-size: 14px;");
        
        content.getChildren().addAll(iconLabel, textLabel);
        btn.setGraphic(content);

        // Colors
        String buttonColor = "#4A90E2";  // Blue color for the buttons
        String activeColor = "#630110";  // Darker blue for active/clicked state
        
        String defaultStyle = 
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-width: 1px;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 0 20px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;";
            
        String hoverStyle = 
            "-fx-background-color: " + PRIMARY_RED + ";" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 0 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(74,144,226,0.3), 6, 0, 0, 0);"+
            "-fx-cursor: hand;";
       
        
   
            
        String activeStyle = 
            "-fx-background-color: " + activeColor + ";" +
            "-fx-border-color: " + activeColor + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-padding: 0 20px;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(46,92,143,0.3), 4, 0, 0, 0);";
        
        // Set initial style based on active state
        btn.setStyle(isActive ? activeStyle : defaultStyle);
        
        // Hover effects
        btn.setOnMouseEntered(e -> {
            if (!isActive) {
                FadeTransition ft = new FadeTransition(Duration.millis(200), btn);
                ft.setFromValue(0.8);
                ft.setToValue(1.0);
                ft.play();
                btn.setStyle(hoverStyle);
            }
        });
        
        btn.setOnMouseExited(e -> {
            if (!isActive) {
                btn.setStyle(defaultStyle);
            }
        });
        
        // Click effect
        btn.setOnMousePressed(e -> {
            if (!isActive) {
                btn.setStyle(activeStyle);
            }
        });
        
        btn.setOnMouseReleased(e -> {
            if (!isActive) {
                btn.setStyle(hoverStyle);
            }
        });

        return btn;
    }

    // Keep the logout button as is, but match its style pattern
    private Button createLogoutButton() {
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setPrefHeight(40);
        
        String defaultStyle = 
            "-fx-background-color: transparent;" +
            "-fx-border-color: #E74C3C;" +  // Danger color
            "-fx-border-width: 1px;" +
            "-fx-text-fill: #E74C3C;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;";
            
        String hoverStyle = 
            "-fx-background-color: #E74C3C;" +
            "-fx-border-color: #E74C3C;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 15;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 8;" +
            "-fx-border-radius: 8;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(231,76,60,0.3), 6, 0, 0, 0);";

        logoutBtn.setStyle(defaultStyle);
        
        logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(hoverStyle));
        logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(defaultStyle));
        
        return logoutBtn;
    }
    private Button createNavButton(String text, String icon) {
        return createNavButton(text, icon, false);
    }
    private HBox createWelcomeSection() {
        HBox welcomeSection = new HBox(20);
        welcomeSection.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeText = new VBox(5);
        Label timeBasedGreeting = new Label(getTimeBasedGreeting() + ", Tala");
        timeBasedGreeting.setStyle(
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + LIGHT_GREY + ";"
        );

        Label dateLabel = new Label(LocalDate.now().format(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy"))
        );
        dateLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-opacity: 0.8;"
        );

        welcomeText.getChildren().addAll(timeBasedGreeting, dateLabel);
        welcomeSection.getChildren().add(welcomeText);

        return welcomeSection;
    }

    private HBox createStatisticsSection() {
        HBox statsContainer = new HBox(20);
        statsContainer.setAlignment(Pos.CENTER);

        // Create animated statistics cards
        statsContainer.getChildren().addAll(
            createAnimatedStatsCard("Total Cars", "150", "🚗", SUCCESS_GREEN, "+12% from last month"),
            createAnimatedStatsCard("Active Customers", "324", "👥", PRIMARY_RED, "+5% from last month"),
            createAnimatedStatsCard("Pending Services", "45", "🔧", WARNING_ORANGE, "8 urgent"),
            createAnimatedStatsCard("Revenue", "$125,000", "💰", SUCCESS_GREEN, "+18% from last month")
        );

        return statsContainer;
    }
    private String getTimeBasedGreeting() {
        int hour = LocalTime.now().getHour();
        if (hour < 12) return "Good Morning";
        if (hour < 18) return "Good Afternoon";
        return "Good Evening";
    }

    private VBox createAnimatedStatsCard(String title, String value, String icon, 
                                       String trendColor, String trend) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setPrefWidth(270);
        card.setMaxWidth(270);
        card.setStyle(
            "-fx-background-color: " + SECONDARY_BLACK + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );

        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + ACCENT_GREY + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 0);"
            );
        });

        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: " + SECONDARY_BLACK + ";" +
                "-fx-background-radius: 15;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
            );
        });

        // Icon and title in HBox
        HBox header = new HBox(10);
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 14px;"
        );
        header.getChildren().addAll(iconLabel, titleLabel);

        // Value with animation
        Label valueLabel = new Label(value);
        valueLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 28px;" +
            "-fx-font-weight: bold;"
        );

        // Trend indicator
        HBox trendBox = new HBox(5);
        trendBox.setAlignment(Pos.CENTER_LEFT);
        
        Label trendIcon = new Label(trendColor.equals(SUCCESS_GREEN) ? "↑" : "↓");
        trendIcon.setStyle(
            "-fx-text-fill: " + trendColor + ";" +
            "-fx-font-size: 16px;"
        );
        
        Label trendLabel = new Label(trend);
        trendLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-opacity: 0.8;"
        );
        
        trendBox.getChildren().addAll(trendIcon, trendLabel);

        card.getChildren().addAll(header, valueLabel, trendBox);
        return card;
    }

    private HBox createChartsSection() {
        HBox chartsSection = new HBox(20);
        chartsSection.setPadding(new Insets(20, 0, 20, 0));
        chartsSection.setAlignment(Pos.CENTER);

        // Sales Chart Card
        VBox salesChart = createChartCard(
            "Sales Overview",
            "Last 6 Months Performance",
            createSampleBarChart()
        );

        // Service Trends Chart
        VBox serviceChart = createChartCard(
            "Service Trends",
            "Monthly Service Distribution",
            createSampleBarChart()
        );

        chartsSection.getChildren().addAll(salesChart, serviceChart);
        return chartsSection;
    }

    private VBox createChartCard(String title, String subtitle, Node chart) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
            "-fx-background-color: " + SECONDARY_BLACK + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );

        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 12px;" +
            "-fx-opacity: 0.8;"
        );

        card.getChildren().addAll(titleLabel, subtitleLabel, chart);
        return card;
    }

    private Node createSampleBarChart() {
        // Create a sample bar chart using JavaFX Charts
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        
        // Style the chart
        barChart.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: white;"
        );
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().addAll(
            new XYChart.Data<>("Jan", 150),
            new XYChart.Data<>("Feb", 180),
            new XYChart.Data<>("Mar", 220),
            new XYChart.Data<>("Apr", 240),
            new XYChart.Data<>("May", 280),
            new XYChart.Data<>("Jun", 310)
        );
        
        barChart.getData().add(series);
        return barChart;
    }

    private HBox createBottomSection() {
        HBox bottomSection = new HBox(20);
        bottomSection.setAlignment(Pos.CENTER);

        // Recent Activities
        VBox recentActivities = createRecentActivitiesCard();
        
        // System Alerts
        VBox systemAlerts = createSystemAlertsCard();

        bottomSection.getChildren().addAll(recentActivities, systemAlerts);
        return bottomSection;
    }

	

    private String formatTimeAgo(Timestamp time) {
        long diffInMillies = System.currentTimeMillis() - time.getTime();
        long diffInMinutes = diffInMillies / (60 * 1000);
        long diffInHours = diffInMinutes / 60;
        long diffInDays = diffInHours / 24;

        if (diffInMinutes < 60) {
            return diffInMinutes + " minutes ago";
        } else if (diffInHours < 24) {
            return diffInHours + " hours ago";
        } else {
            return diffInDays + " days ago";
        }
    }
    private VBox createProfileSection() {
        VBox profileSection = new VBox(10);
        profileSection.setPadding(new Insets(15, 0, 15, 0));

        Circle profilePic = new Circle(30);
        profilePic.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/images.jpg"))));
        
        Label nameLabel = new Label("Tala");
        nameLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + LIGHT_GREY + ";"
        );

        Label roleLabel = new Label("Administrator");
        roleLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + PRIMARY_RED + ";"
        );

        profileSection.setAlignment(Pos.CENTER);
        profileSection.getChildren().addAll(profilePic, nameLabel, roleLabel);

        return profileSection;
    }

    private VBox createNavigationSection() {
        VBox navigation = new VBox(5);
        navigation.setPadding(new Insets(10, 0, 10, 0));

        Label navLabel = new Label("NAVIGATION");
        navLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-opacity: 0.6;"
        );

        navigation.getChildren().add(navLabel);
        navigation.getChildren().addAll(
            createNavButton("Dashboard", "dashboard", true),
            createNavButton("Inventory", "directions_car"),
            createNavButton("Customers", "people"),
            createNavButton("Sales", "point_of_sale"),
            createNavButton("Services", "build"),
            createNavButton("Reports", "analytics")
        );

        return navigation;
    }

   
    
    private VBox createQuickActionsSection() {
        VBox quickActions = new VBox(5);
        quickActions.setPadding(new Insets(10, 0, 10, 0));

        Label quickLabel = new Label("QUICK ACTIONS");
        quickLabel.setStyle(
            "-fx-font-size: 12px;" +
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-opacity: 0.6;"
        );

        quickActions.getChildren().add(quickLabel);
        quickActions.getChildren().addAll(
            createQuickActionButton("Add New Car", "add_circle"),
            createQuickActionButton("New Service", "build_circle"),
            createQuickActionButton("Create Report", "assessment")
        );

        return quickActions;
    }

   
    private Button createQuickActionButton(String text, String iconName) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Label icon = new Label(iconName);
        icon.setStyle(
            "-fx-font-family: 'Material Icons';" +
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + PRIMARY_RED + ";"
        );

        Label label = new Label(text);
        label.setStyle("-fx-text-fill: " + LIGHT_GREY + ";");

        content.getChildren().addAll(icon, label);
        btn.setGraphic(content);
        
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-radius: 5;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10 15;"
        );
        
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + PRIMARY_RED + ";" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-radius: 5;" +
            "-fx-padding: 10 15;"
        ));
        
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-radius: 5;" +
            "-fx-padding: 10 15;"
        ));

        return btn;
    }

   
    private MenuButton createUserMenu() {
        MenuButton userMenu = new MenuButton();
        
        HBox content = new HBox(10);
        content.setAlignment(Pos.CENTER_LEFT);
        
        Circle userAvatar = new Circle(15);
        userAvatar.setFill(new ImagePattern(new Image(getClass().getResourceAsStream("/images.jpg"))));
        
        VBox userInfo = new VBox(0);
        Label userName = new Label("Tala");
        Label userRole = new Label("Administrator");
        userName.setStyle("-fx-text-fill: " + LIGHT_GREY + "; -fx-font-weight: bold;");
        userRole.setStyle("-fx-text-fill: " + PRIMARY_RED + "; -fx-font-size: 11px;");
        userInfo.getChildren().addAll(userName, userRole);
        
        content.getChildren().addAll(userAvatar, userInfo);
        userMenu.setGraphic(content);
        
        // Menu items
        MenuItem profile = new MenuItem("Profile Settings");
        MenuItem preferences = new MenuItem("Preferences");
        MenuItem logout = new MenuItem("Logout");
        
        userMenu.getItems().addAll(profile, preferences, new SeparatorMenuItem(), logout);
        
        return userMenu;
    }

    private HBox createBreadcrumb() {
        HBox breadcrumb = new HBox(10);
        breadcrumb.setPadding(new Insets(10, 30, 10, 30));
        breadcrumb.setAlignment(Pos.CENTER_LEFT);

        Label dashboardLabel = new Label("Dashboard");
        dashboardLabel.setStyle("-fx-text-fill: " + PRIMARY_RED + ";");
        
        Label separator = new Label(">");
        separator.setStyle("-fx-text-fill: " + LIGHT_GREY + ";");
        
        Label currentPage = new Label("Overview");
        currentPage.setStyle("-fx-text-fill: " + LIGHT_GREY + ";");

        breadcrumb.getChildren().addAll(dashboardLabel, separator, currentPage);
        return breadcrumb;
    }

   
    private VBox createRecentActivitiesCard() {
        VBox card = new VBox(15);
        // ... card styling ...

        Label titleLabel = new Label("Recent Activities");
        // ... title styling ...

        VBox activitiesList = new VBox(10);
        ArrayList<DashboardASU.ActivityRecord> recentActivities = DashboardASU.getRecentActivities(5);
        
        for (DashboardASU.ActivityRecord activity : recentActivities) {
            String icon = activity.getType().equals("Order") ? "📦" : "🔧";
            activitiesList.getChildren().add(
                createActivityItem(
                    activity.getType(),
                    activity.getDescription(),
                    formatTimeAgo(activity.getDate()),
                    icon
                )
            );
        }

        card.getChildren().addAll(titleLabel, new Separator(), activitiesList);
        return card;
    }

    private HBox createActivityItem(String title, String description, String time, String icon) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 0, 10, 0));

        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 20px;"
        );

        // Text content
        VBox textContent = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );

        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 12px;"
        );

        Label timeLabel = new Label(time);
        timeLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 11px;" +
            "-fx-opacity: 0.8;"
        );

        textContent.getChildren().addAll(titleLabel, descLabel, timeLabel);
        item.getChildren().addAll(iconLabel, textContent);

        return item;
    }

    private VBox createSystemAlertsCard() {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setPrefWidth(500);
        card.setStyle(
            "-fx-background-color: " + SECONDARY_BLACK + ";" +
            "-fx-background-radius: 15;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);"
        );

        // Header
        Label titleLabel = new Label("System Alerts");
        titleLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;"
        );

        // Alert items
        VBox alertsList = new VBox(10);
        alertsList.getChildren().addAll(
            createAlertItem("Low Inventory Alert", "5 items below threshold", WARNING_ORANGE),
            createAlertItem("Service Due", "3 vehicles need maintenance", PRIMARY_RED),
            createAlertItem("System Update", "New version available", SUCCESS_GREEN)
        );

        card.getChildren().addAll(titleLabel, new Separator(), alertsList);
        return card;
    }

    private HBox createAlertItem(String title, String description, String color) {
        HBox item = new HBox(15);
        item.setAlignment(Pos.CENTER_LEFT);
        item.setPadding(new Insets(10, 0, 10, 0));

        // Alert indicator
        Circle indicator = new Circle(5);
        indicator.setFill(Paint.valueOf(color));

        // Text content
        VBox textContent = new VBox(3);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;"
        );

        Label descLabel = new Label(description);
        descLabel.setStyle(
            "-fx-text-fill: " + LIGHT_GREY + ";" +
            "-fx-font-size: 12px;"
        );

        textContent.getChildren().addAll(titleLabel, descLabel);
        item.getChildren().addAll(indicator, textContent);

        return item;
    }
    
    private void handleNavigation(String destination) {
        // Clear previous center content with a nice fade
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), getCenter());
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        fadeOut.setOnFinished(e -> {
            switch (destination) {
                case "Cars":
                    // Create and set the car table view
                    BorderPane carTableView = CarTableView.buildCarTableView();
                    setCenter(carTableView);
                    // Fade in the new content
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(200), carTableView);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                    break;
                    
                case "Dashboard":
                    setCenter(createWelcomeContent());
                    break;
                    
                // Add other cases for different pages
                default:
                    setCenter(createWelcomeContent());
                    break;
            }
        });

        fadeOut.play();
    }
}