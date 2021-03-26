import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GUI {
    private final String[] catgsArr = {"Active Life", "Arts & Entertainment", "Automotive", "Car Rental", "Cafes",
            "Beauty & Spas", "Convenience Stores", "Dentists", "Doctors","Drugstores","Department Stores",
            "Education","Event Planning & Services","Flowers & Gifts","Food","Health & Medical","Home Services",
            "Home & Garden","Hospitals", "Hotels & Travel", "Hardware Stores","Grocery", "Medical Centers",
            "Nurseries & Gardening","Nightlife","Restaurants","Shopping","Transportation"};
    private final String[] comparisonArr = {" < ", " <= ", " = ", " > ", " >= "};
    private final DBConnection dbConnection;
    private final String busnResultQueryPrefix = "SELECT bid, bname AS Business, city, b_state AS State, stars FROM Business WHERE bid IN (\n";
    private final JFrame window;

    private JTable resultTable, secondResultTable;
    private JTextArea queryTextArea;
    private JPanel topPanel, topLeftPanel, topRightPanel, bottomLeftPanel, bottomRightPanel, catgPanel,
            subcatgPanel, subcatgContentPanel, attributePanel, attributeContentPanel, businessSetOperatorPanel,
            reviewPanel, reviewContentPanel, datePanel, starPanel, votePanel, resultPanel, secondResultPanel,
            queryPanel, userPanel, userBottomPanel;
    private DatePicker datePickerFrom, datePickerTo, memberSinceDatePicker;
    private JTextField reviewCountTextField, friendCountTextField, avgStarsTextField, voteCountTextField;
    private ArrayList<JCheckBox> catgCheckBoxList, subcatgCheckBoxList, attributeCheckBoxList;
    private String businessSetOperatorStr, userSetOperatorStr;
    private StringBuilder catgQuery, subcatgQuery, attributeQuery, catgResultQuery, subcatgResultQuery, attributeResultQuery;
    private JComboBox<String> reviewCountComboBox, numOfFriendsComboBox, avgStarsComboBox, numOfVotesComboBox;
    public DefaultTableModel tableModel, secondTableModel;
    private boolean isUserSearch;
    private String[] reviewQueryPostfix = new String[4];



    public GUI() {
        dbConnection = new DBConnection();
        window = new JFrame();
        createPanels();
        window.setContentPane(topPanel);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setSize(1500, 1000);
        window.setTitle("Yelp Business and User Search");
        window.setVisible(true);
    }

    private void createPanels() {
        topPanel = new JPanel(new GridLayout(2,2, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        createTopLeftPanel();
        topPanel.add(topLeftPanel);
        createTopRightPanel();
        topPanel.add(topRightPanel);
        createBottomLeftPanel();
        topPanel.add(bottomLeftPanel);
        createBottomRightPanel();
        topPanel.add(bottomRightPanel);
    }

    private void createTopLeftPanel() {
        topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel busnTitlePanel = new JPanel();
        busnTitlePanel.add(new JLabel("Business"));
        busnTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        topLeftPanel.add(busnTitlePanel, BorderLayout.NORTH);

        JPanel businessPanel = new JPanel(new GridLayout(1, 3));
        createCategoryPanel();
        businessPanel.add(catgPanel);
        createSubcategoryPanel();
        businessPanel.add(subcatgPanel);
        createAttributePanel();
        businessPanel.add(attributePanel);
        topLeftPanel.add(businessPanel, BorderLayout.CENTER);

        createBusinessSetOperatorPanel();
        topLeftPanel.add(businessSetOperatorPanel, BorderLayout.SOUTH);
    }

    private void createBusinessSetOperatorPanel() {
        businessSetOperatorPanel = new JPanel();
        businessSetOperatorPanel.setBorder(BorderFactory.createEtchedBorder());
        JLabel searchForLabel = new JLabel("Search for");
        businessSetOperatorPanel.add(searchForLabel);

        JRadioButton andButton = new JRadioButton("AND");
        JRadioButton orButton = new JRadioButton("OR");
        ButtonGroup group = new ButtonGroup();
        group.add(andButton);
        group.add(orButton);
        andButton.setSelected(true);
        businessSetOperatorPanel.add(andButton);
        businessSetOperatorPanel.add(orButton);

        businessSetOperatorStr = "\nINTERSECT\n";
        andButton.addItemListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                businessSetOperatorStr = "\nINTERSECT\n";
            }
        });
        orButton.addItemListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                businessSetOperatorStr = "\nUNION\n";
            }
        });
    }

    private void createCategoryPanel() {
        catgPanel = new JPanel(new BorderLayout());

        JPanel catgTitlePanel = new JPanel();
        catgTitlePanel.add(new JLabel("Category"));
        catgTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        catgPanel.add(catgTitlePanel, BorderLayout.NORTH);

        JPanel catgContentPanel = new JPanel();
        catgContentPanel.setLayout(new BoxLayout(catgContentPanel, BoxLayout.Y_AXIS));
        catgCheckBoxList = new ArrayList<>();
        for (String catg : catgsArr) {
            JCheckBox checkBox = new JCheckBox(catg, false);
            catgCheckBoxList.add(checkBox);
            catgContentPanel.add(checkBox);
        }
        for (JCheckBox checkBox : catgCheckBoxList) {
            checkBox.addItemListener(e -> categorySearch());
        }

        JScrollPane catgScrollPane = new JScrollPane(catgContentPanel);
        catgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        catgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        catgPanel.add(catgScrollPane, BorderLayout.CENTER);
    }

    private void createSubcategoryPanel(){
        subcatgPanel = new JPanel(new BorderLayout());

        JPanel subcatgTitlePanel = new JPanel();
        subcatgTitlePanel.add(new JLabel("Sub-category"));
        subcatgTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        subcatgPanel.add(subcatgTitlePanel, BorderLayout.NORTH);

        subcatgContentPanel = new JPanel();
        subcatgContentPanel.setLayout(new BoxLayout(subcatgContentPanel, BoxLayout.Y_AXIS));
        JScrollPane subcatgScrollPane = new JScrollPane(subcatgContentPanel);
        subcatgScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        subcatgScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        subcatgPanel.add(subcatgScrollPane, BorderLayout.CENTER);
    }

    private void createAttributePanel() {
        attributePanel = new JPanel(new BorderLayout());

        JPanel attributeTitlePanel = new JPanel();
        attributeTitlePanel.add(new JLabel("Attribute"));
        attributeTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        attributePanel.add(attributeTitlePanel, BorderLayout.NORTH);

        attributeContentPanel = new JPanel();
        attributeContentPanel.setLayout(new BoxLayout(attributeContentPanel, BoxLayout.Y_AXIS));
        JScrollPane attributeScrollPane = new JScrollPane(attributeContentPanel);
        attributeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        attributeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        attributePanel.add(attributeScrollPane, BorderLayout.CENTER);
    }

    private void createTopRightPanel() {
        topRightPanel = new JPanel(new BorderLayout());
        createResultPanel();
        topRightPanel.add(resultPanel, BorderLayout.CENTER);
    }

    private void createResultPanel(){
        resultPanel = new JPanel(new BorderLayout());

        JPanel resultTitlePanel = new JPanel();
        resultTitlePanel.add(new JLabel("Result"));
        resultTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        resultPanel.add(resultTitlePanel, BorderLayout.NORTH);

        resultTable  = new JTable();
        resultTable.setGridColor(Color.LIGHT_GRAY);
        resultTable.setFocusable(false);

        resultTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel selectionModel = (ListSelectionModel) e.getSource();
                    int rowIndex = selectionModel.getMinSelectionIndex();
                    if (rowIndex >= 0) {
                        String str = tableModel.getValueAt(rowIndex, 0).toString();
                        StringBuilder query = new StringBuilder();
                        if (isUserSearch) {
                            query.append("SELECT R.rdate AS Review_Date, " +
                                    "R.stars AS Stars, R.content AS Review_Text, B.bname as Business_Name, " +
                                    "R.useful_vote_count AS Useful_Vote FROM Review R, Business B " +
                                    "WHERE R.user_id = '");
                            query.append(str);
                            query.append("' AND R.bid = B.bid");
                        } else {
                            query.append("SELECT R.rdate AS Review_Date, " +
                                    "R.stars AS Stars, R.content AS Review_Text, U.name as User_Name, " +
                                    "R.useful_vote_count AS Useful_Vote FROM Review R, Yelp_User U " +
                                    "WHERE R.bid = '");
                            query.append(str);
                            query.append("' AND R.user_id = U.user_id");
                            for (String postfix : reviewQueryPostfix) {
                                if (postfix != null && postfix.length() != 0)
                                    query.append(postfix);
                            }
                        }
                        System.out.println(query.toString());
                        secondTableModel= dbConnection.QueryExecution(query.toString());
                        secondResultTable.setModel(secondTableModel);
                    } else {
                        secondResultTable.setModel(new DefaultTableModel());
                    }
                }
            }
        });

        JTableHeader header = resultTable.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        header.setForeground(Color.BLACK);

        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        resultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
    }

    private void createBottomLeftPanel() {
        bottomLeftPanel = new JPanel(new BorderLayout());
        bottomLeftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        createReviewPanel();
        bottomLeftPanel.add(reviewPanel, BorderLayout.NORTH);

        createUserPanel();
        bottomLeftPanel.add(userPanel, BorderLayout.CENTER);
    }

    private void createReviewPanel() {
        reviewPanel = new JPanel(new BorderLayout());
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel reviewTitlePanel = new JPanel();
        reviewTitlePanel.add(new JLabel("Review"));
        reviewTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        reviewPanel.add(reviewTitlePanel, BorderLayout.NORTH);

        createReviewContentPanel();
        reviewPanel.add(reviewContentPanel, BorderLayout.CENTER);
    }

    private void createReviewContentPanel() {
        reviewContentPanel = new JPanel(new GridLayout(1, 3));
        createDatePanel();
        reviewContentPanel.add(datePanel);

        createStarPanel();
        reviewContentPanel.add(starPanel);

        createVotePanel();
        reviewContentPanel.add(votePanel);
    }

    private void createDatePanel() {
        datePanel = new JPanel(new GridLayout(2, 1));
        datePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel fromPanel = new JPanel(new GridLayout(2, 1));
        fromPanel.setBorder(BorderFactory.createEmptyBorder(0, 5,10, 5));
        JLabel fromLabel = new JLabel("From: ");
        fromPanel.add(fromLabel);
        datePickerFrom = new DatePicker();
        datePickerFrom.addDateChangeListener(dateChangeEvent -> {
            if (datePickerFrom.getDate() != null) {
                reviewQueryPostfix[0]= " AND R.rdate >= To_DATE('" +
                        datePickerFrom.getDateStringOrEmptyString() + "', 'YYYY-MM-DD')";
            } else {
                reviewQueryPostfix[0] = null;
            }
        });
        fromPanel.add(datePickerFrom);
        datePanel.add(fromPanel);

        JPanel toPanel = new JPanel(new GridLayout(2, 1));
        toPanel.setBorder(BorderFactory.createEmptyBorder(0, 5,10, 5));
        JLabel toLabel = new JLabel("To: ");
        toPanel.add(toLabel);
        datePickerTo = new DatePicker();
        datePickerTo.addDateChangeListener(new DateChangeListener() {
            @Override
            public void dateChanged(DateChangeEvent dateChangeEvent) {
                if (datePickerTo.getDate() != null) {
                    reviewQueryPostfix[1] = " AND R.rdate <= To_DATE('" +
                            datePickerTo.getDateStringOrEmptyString() + "', 'YYYY-MM-DD')";
                } else {
                    reviewQueryPostfix[1] = null;
                }
            }
        });
        toPanel.add(datePickerTo);
        datePanel.add(toPanel);
    }

    private void createStarPanel() {
        starPanel = new JPanel(new GridLayout(2, 0));
        starPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel starRowPanel = new JPanel(new GridLayout(2, 1));
        starRowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,5, 10));
        starRowPanel.add(new JLabel("Star: "));
        JComboBox<String> starComboBox = new JComboBox<>(comparisonArr);
        starComboBox.setSelectedIndex(0);
        starRowPanel.add(starComboBox);
        starPanel.add(starRowPanel);

        JPanel valueRowPanel = new JPanel(new GridLayout(2, 1));
        valueRowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,5, 10));
        valueRowPanel.add(new JLabel("Value: "));
        JTextField reviewStarTextField = new JTextField();
        reviewStarTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (reviewStarTextField.getText().length() != 0) {
                    reviewQueryPostfix[2] = " AND R.stars" +
                            comparisonArr[starComboBox.getSelectedIndex()] +
                            Double.parseDouble(reviewStarTextField.getText());
                } else {
                    reviewQueryPostfix[2] = null;
                }
            }
        });
        valueRowPanel.add(reviewStarTextField);
        starPanel.add(valueRowPanel);
    }

    private void createVotePanel() {
        votePanel = new JPanel(new GridLayout(2, 0));
        votePanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel voteRowPanel = new JPanel(new GridLayout(2, 1));
        voteRowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,5, 10));
        voteRowPanel.add(new JLabel("Vote: "));
        JComboBox<String> voteComboBox = new JComboBox<>(comparisonArr);
        voteComboBox.setSelectedIndex(0);
        voteRowPanel.add(voteComboBox);
        votePanel.add(voteRowPanel);

        JPanel valueRowPanel = new JPanel(new GridLayout(2,1));
        valueRowPanel.setBorder(BorderFactory.createEmptyBorder(5, 10,5, 10));
        valueRowPanel.add(new JLabel("Value: "));
        JTextField reviewVoteTextField = new JTextField();
        reviewVoteTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (reviewVoteTextField.getText().length() != 0) {
                    reviewQueryPostfix[3] = " AND R.useful_vote_count" + comparisonArr[voteComboBox.getSelectedIndex()] +
                            Integer.parseInt(reviewVoteTextField.getText());
                } else {
                    reviewQueryPostfix[3] = null;
                }
            }
        });
        valueRowPanel.add(reviewVoteTextField);
        votePanel.add(valueRowPanel);
    }

    private void createUserPanel() {
        userPanel = new JPanel(new BorderLayout());
        userPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel userTitlePanel = new JPanel();
        userTitlePanel.add(new JLabel("Users"));
        userTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        userPanel.add(userTitlePanel, BorderLayout.NORTH);

        JPanel userContentPanel = new JPanel(new GridLayout(1, 3));
        userContentPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel p1 = new JPanel();
        p1.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 0));
        p1.setLayout(new GridLayout(5, 1));
        p1.add(new JLabel("Member Since"));
        p1.add(new JLabel("Review Count"));
        p1.add(new JLabel("Number of Friends"));
        p1.add(new JLabel("Average Stars"));
        p1.add(new JLabel("Number of Votes"));
        userContentPanel.add(p1);

        JPanel p2 = new JPanel();
        p2.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 10));
        p2.setLayout(new GridLayout(5, 1));
        memberSinceDatePicker = new DatePicker();
        p2.add(memberSinceDatePicker);

        reviewCountComboBox = new JComboBox<>(comparisonArr);
        p2.add(reviewCountComboBox);

        numOfFriendsComboBox = new JComboBox<>(comparisonArr);
        p2.add(numOfFriendsComboBox);

        avgStarsComboBox = new JComboBox<>(comparisonArr);
        p2.add(avgStarsComboBox);

        numOfVotesComboBox = new JComboBox<>(comparisonArr);
        p2.add(numOfVotesComboBox);
        userContentPanel.add(p2);

        JPanel p3 = new JPanel();
        p3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        p3.setLayout(new GridLayout(5, 1));
        JPanel row1Panel = new JPanel();
        p3.add(row1Panel);

        JPanel row2Panel = new JPanel(new BorderLayout());
        row2Panel.add(new JLabel("Value: "), BorderLayout.WEST);
        reviewCountTextField = new JTextField(10);
        row2Panel.add(reviewCountTextField);
        p3.add(row2Panel);

        JPanel row3Panel = new JPanel(new BorderLayout());
        row3Panel.add(new JLabel("Value: "), BorderLayout.WEST);
        friendCountTextField = new JTextField(10);
        row3Panel.add(friendCountTextField);
        p3.add(row3Panel);

        JPanel row4Panel = new JPanel(new BorderLayout());
        row4Panel.add(new JLabel("Value: "), BorderLayout.WEST);
        avgStarsTextField = new JTextField(10);
        row4Panel.add(avgStarsTextField);
        p3.add(row4Panel);

        JPanel row5Panel = new JPanel(new BorderLayout());
        row5Panel.add(new JLabel("Value: "), BorderLayout.WEST);
        voteCountTextField = new JTextField(10);
        row5Panel.add(voteCountTextField);
        p3.add(row5Panel);

        userContentPanel.add(p3);
        userPanel.add(userContentPanel, BorderLayout.CENTER);

        createUserBottomPanel();
        userPanel.add(userBottomPanel, BorderLayout.SOUTH);

    }

    private void createUserBottomPanel() {
        userBottomPanel = new JPanel();
        userBottomPanel.setBorder(BorderFactory.createEtchedBorder());

        createUserSetOperatorPanel();

        JButton executionButton = new JButton("Execute User Query");
        executionButton.addActionListener(e -> {
            if (memberSinceDatePicker.getDate() == null || reviewCountTextField.getText().length() == 0 ||
                    friendCountTextField.getText().length() == 0 || avgStarsTextField.getText().length() == 0 ||
                    voteCountTextField.getText().length() == 0) {
                JOptionPane.showMessageDialog(window, "Incomplete Input!\nPlease specify all the search "
                        + "criteria.", "Invalid User Search", JOptionPane.WARNING_MESSAGE);
            } else {
                isUserSearch = true;
                StringBuilder query = new StringBuilder();
                query.append("SELECT * FROM Yelp_User WHERE yelping_since >= TO_DATE('");
                query.append(memberSinceDatePicker.getDateStringOrEmptyString());
                query.append("', 'YYYY-MM-DD')");
                query.append(userSetOperatorStr);
                query.append("review_count");
                query.append(comparisonArr[reviewCountComboBox.getSelectedIndex()]);
                query.append(reviewCountTextField.getText());
                query.append(userSetOperatorStr);
                query.append("friend_count");
                query.append(comparisonArr[numOfFriendsComboBox.getSelectedIndex()]);
                query.append(friendCountTextField.getText());
                query.append(userSetOperatorStr);
                query.append("avg_stars");
                query.append(comparisonArr[avgStarsComboBox.getSelectedIndex()]);
                query.append(avgStarsTextField.getText());
                query.append(userSetOperatorStr);
                query.append("vote_count");
                query.append(comparisonArr[numOfVotesComboBox.getSelectedIndex()]);
                query.append(voteCountTextField.getText());
                tableModel = dbConnection.QueryExecution(query.toString());
                resultTable.setModel(tableModel);
                queryTextArea.setText(query.toString());
            }
        });
        userBottomPanel.add(executionButton);
    }

    private void createUserSetOperatorPanel() {
        JLabel searchForLabel = new JLabel("Search for");
        userBottomPanel.add(searchForLabel);

        JRadioButton andButton = new JRadioButton("AND");
        JRadioButton orButton = new JRadioButton("OR");
        ButtonGroup group = new ButtonGroup();
        group.add(andButton);
        group.add(orButton);
        andButton.setSelected(true);
        userBottomPanel.add(andButton);
        userBottomPanel.add(orButton);

        userSetOperatorStr = " AND ";
        andButton.addItemListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                userSetOperatorStr = " AND ";
            }
        });
        orButton.addItemListener(e -> {
            if (((JRadioButton) e.getSource()).isSelected()) {
                userSetOperatorStr = " OR ";
            }
        });
    }

    private void createBottomRightPanel() {
        bottomRightPanel = new JPanel(new GridLayout(2, 1));
        createSecondResultPanel();
        bottomRightPanel.add(secondResultPanel);
        createQueryPanel();
        bottomRightPanel.add(queryPanel);
    }

    private void createSecondResultPanel() {
        secondResultPanel = new JPanel(new BorderLayout());
        secondResultPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel secondResultTitlePanel = new JPanel();
        secondResultTitlePanel.setBorder(BorderFactory.createEtchedBorder());
        secondResultTitlePanel.add(new JLabel("Second Result"));
        secondResultPanel.add(secondResultTitlePanel, BorderLayout.NORTH);


        secondResultTable = new JTable();
        secondResultTable.setGridColor(Color.LIGHT_GRAY);
        secondResultTable.setFocusable(false);

        JTableHeader header = secondResultTable.getTableHeader();
        header.setBackground(Color.LIGHT_GRAY);
        header.setForeground(Color.BLACK);

        JScrollPane secondResultScrollPane = new JScrollPane(secondResultTable);
        secondResultScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        secondResultScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        secondResultPanel.add(secondResultScrollPane, BorderLayout.CENTER);
    }

    private void createQueryPanel(){
        queryPanel = new JPanel(new BorderLayout());
        queryPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel showQueryLabelPanel = new JPanel();
        showQueryLabelPanel.setBorder(BorderFactory.createEtchedBorder());
        showQueryLabelPanel.add(new JLabel("Query Display"));
        queryPanel.add(showQueryLabelPanel, BorderLayout.NORTH);

        JPanel queryDisplayPanel = new JPanel(new BorderLayout());
        queryTextArea = new JTextArea();
        queryTextArea.setLineWrap(true);
        JScrollPane textAreaScrollPane = new JScrollPane(queryTextArea);
        textAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        queryDisplayPanel.add(textAreaScrollPane, BorderLayout.CENTER);
        queryPanel.add(queryDisplayPanel, BorderLayout.CENTER);
    }

    private void categorySearch() {
        queryTextArea.setText("");
        subcatgContentPanel.removeAll();
        subcatgContentPanel.revalidate();
        subcatgContentPanel.repaint();
        attributeContentPanel.removeAll();
        attributeContentPanel.revalidate();
        attributeContentPanel.repaint();
        resultTable.setModel(new DefaultTableModel());
        isUserSearch = false;

        ArrayList<String> selectedCatgs = new ArrayList<>();
        for (JCheckBox checkbox : catgCheckBoxList) {
            if (checkbox.isSelected()) {
                selectedCatgs.add(checkbox.getText());
            }
        }

        int size = selectedCatgs.size();
        if (size > 0) {
            catgResultQuery = new StringBuilder();
            catgResultQuery.append(busnResultQueryPrefix);

            catgQuery = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    catgQuery.append(businessSetOperatorStr);
                }
                catgQuery.append("SELECT bid FROM Category WHERE catg = '");
                catgQuery.append(selectedCatgs.get(i));
                catgQuery.append("'");
            }
            catgResultQuery.append(catgQuery);

            catgResultQuery.append(")\n");
            queryTextArea.setText(catgResultQuery.toString());
            tableModel= dbConnection.QueryExecution(catgResultQuery.toString());
            resultTable.setModel(tableModel);
            updateSubcategoryContentPanel();
        }
    }

    private void updateSubcategoryContentPanel() {
        ArrayList<String> subcatgs = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT subcatg FROM Subcategory WHERE bid IN (");
        sb.append(catgQuery);
        sb.append(") ORDER BY subcatg");

        try {
            ResultSet resultSet = dbConnection.statement.executeQuery(sb.toString());
            while (resultSet.next()) {
                String subcatg = resultSet.getString("subcatg");
                subcatgs.add(subcatg);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        subcatgCheckBoxList = new ArrayList<>();
        for (String subcatg : subcatgs) {
            JCheckBox checkBox = new JCheckBox(subcatg, false);
            subcatgContentPanel.add(checkBox);
            subcatgCheckBoxList.add(checkBox);
            checkBox.addItemListener(e -> subcategorySearch());
        }
    }

    private void subcategorySearch() {
        attributeContentPanel.removeAll();
        attributeContentPanel.revalidate();
        attributeContentPanel.repaint();
        queryTextArea.setText(catgResultQuery.toString());
        tableModel = dbConnection.QueryExecution(catgResultQuery.toString());
        resultTable.setModel(tableModel);
        isUserSearch = false;

        ArrayList<String> selectedSubcatgs = new ArrayList<>();
        for (JCheckBox checkBox : subcatgCheckBoxList) {
            if (checkBox.isSelected()) {
                selectedSubcatgs.add(checkBox.getText());
            }
        }
        int size = selectedSubcatgs.size();
        if (size > 0) {
            subcatgResultQuery = new StringBuilder();
            subcatgResultQuery.append(busnResultQueryPrefix);

            subcatgQuery = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    subcatgQuery.append(businessSetOperatorStr);
                }
                subcatgQuery.append("SELECT bid FROM Subcategory WHERE bid IN (\n");
                subcatgQuery.append(catgQuery);
                subcatgQuery.append(") AND subcatg = '");
                subcatgQuery.append(selectedSubcatgs.get(i));
                subcatgQuery.append("'");
            }
            subcatgResultQuery.append(subcatgQuery);

            subcatgResultQuery.append(")");
            queryTextArea.setText(subcatgResultQuery.toString());
            tableModel= dbConnection.QueryExecution(subcatgResultQuery.toString());
            resultTable.setModel(tableModel);
            updateAttributePanel();
        }
    }

    private void updateAttributePanel() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT DISTINCT attribute FROM Attribute WHERE bid IN (");
        sb.append(subcatgQuery);
        sb.append(") ORDER BY attribute");
        ArrayList<String> attributes = new ArrayList<>();
        try {
            ResultSet resultSet = dbConnection.statement.executeQuery(sb.toString());
            while (resultSet.next()) {
                String attribute = resultSet.getString("attribute");
                attributes.add(attribute);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        attributeCheckBoxList = new ArrayList<>();
        for (String attribute : attributes) {
            JCheckBox checkBox = new JCheckBox(attribute, false);
            attributeContentPanel.add(checkBox);
            attributeCheckBoxList.add(checkBox);
            checkBox.addItemListener(e -> attributeSearch());
        }
    }

    private void attributeSearch() {
        queryTextArea.setText(subcatgResultQuery.toString());
        tableModel = dbConnection.QueryExecution(subcatgResultQuery.toString());
        resultTable.setModel(tableModel);
        isUserSearch = false;

        ArrayList<String> selectedAttributes = new ArrayList<>();
        for (JCheckBox checkBox : attributeCheckBoxList) {
            if (checkBox.isSelected()) {
                selectedAttributes.add(checkBox.getText());
            }
        }
        int size = selectedAttributes.size();
        if (size > 0) {
            attributeResultQuery = new StringBuilder();
            attributeResultQuery.append(busnResultQueryPrefix);

            attributeQuery = new StringBuilder();
            for (int i = 0; i < size; i++) {
                if (i > 0) {
                    attributeQuery.append(businessSetOperatorStr);
                }
                attributeQuery.append("SELECT bid FROM Attribute WHERE bid IN (\n");
                attributeQuery.append(subcatgQuery);
                attributeQuery.append(") AND attribute = '");
                attributeQuery.append(selectedAttributes.get(i));
                attributeQuery.append("'");

            }
            attributeResultQuery.append(attributeQuery);

            attributeResultQuery.append(")");
            queryTextArea.setText(attributeResultQuery.toString());
            tableModel = dbConnection.QueryExecution(attributeResultQuery.toString());
            resultTable.setModel(tableModel);
        }
    }

    public static void main(String[] args) {
        GUI hw3program = new GUI();
    }
}
