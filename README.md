# Yelp
## OverView
Yelp is a standalone Java application that queries a Yelp dataset to extract useful information for local businesses and individual users. The primary users of this applicaton will be potentional customers seeking for businesses and users that match their search criteria. Yelp has a GUI that provides the user the available business categories (main, sub-categories), the attributes associated with each business category along with business review and yelp user information associated with each business category. 

## Dataset
The Yelp data is available in JSON format. The original Yelp dataset includes 42,153 businesses, 252,898 users, and 1,125,458 reviews from Phoenix (AZ), Las Vegas (NV), Madison (WI) in United States and Waterloo (ON) and Edinburgh (ON) in Canada. This project uses a smaller and simplified dataset which includes 20,544 businesses, the reviews that are written for those businesses only, and the users that wrote those reviews.

## Functionality
### Business Search
With the GUI of this application, the user can filter the search results using available business attributes (i.e. facets) such as category, sub-category, attributes, reviews, stars and votes. Each time the user clicks on a facet value, the set of results is reduced to only the items that have that value. Additional clicks continue to narrow down the search — the previous facet values are remembered and applied again.
### User Search
With the GUI of this application, the user can perform a searach of users that match the criteria given. 
The usage flow of the GUI is as follows:
  1. User can specify user search using attributes such as member_since, review_count, number of friends, verage stars and number of friends.
  2. Clicking on “Execute User Query” will show user matches, yelping_since and average_stars
  3. select a certain user in the search results and list all the reviews given by that user.

## Screenshots
![](../master/screenshots/1.png)
![](../master/screenshots/2.png)
![](../master/screenshots/3.png)
