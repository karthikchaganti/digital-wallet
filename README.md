# Digital Wallet - Insight Data Engineering Coding Challenge
### by Karthik Chaganti

### Table of Contents

1. [Approach to the problem] (README.md#Approach-to-the-problem)
2. [Details of Implementation] (README.md#details-of-implementation)
3. [Features] (README.md#feature-1)
4. [How to Run?] (README.md#how-to-run)
5. [Description of Data] (README.md#description-of-data)
6. [Original Problem Description] (README.md#original-problem-description)

## Approach to the problem
To infer or find the relationships between different nodes or users, Graph algorithms serve as the one of the best recipes. Here, since there are huge number of users and multiple transactions between them, no other datastructure can suit better than graphs. As each user is assigned an unique ID, we can use them to act as distinct vertices of a graph and transaction between them as edges. This vertex-edge relationship can be captured using Adjacency List representation. Adjacency list is better compared to matrix as the no.of transactions in the dataset is in order of millions and moreover there can be multiple transactions between each node. 
* A HashMap is used to hold the userID and an object that comprises of it's friends list (with whom the user had direct transaction) and some info necessary to perform some inference operations. The friends list is built using HashSet as for the algorithm I chose, multiple transactions between same users are not necessary. And moreover the search in hashset is constant time. HashMap is used as the lookup speeds at different users' data would be O(1) and its easy to store as well as easy to port to servers.

## Details of Implementation
[Back to Table of Contents] (README.md#table-of-contents)
### Modelling of Graph
By reading the batch_payments, the graph is modelled based on the transactions between different users. An edge between two users is bi-directional, so user-1 will have user-2 in his friend's list and vice-versa. 
* Post building the graph, the median degree of all the users in the graph is calculated for future reference.
Once the graph is created, `stream_payment.txt` is read line by line and the users in each transaction are passed onto the graph to check for features.

### Feature 1
If the requester is a friend of payer, then it is a trusted transaction of degree 1. So the user-1's adjacency list is directly retrieved and checked if it contains user-2. 
* Intuitively we can say that a direct friend is of degree 1. So it should even satisfy the other two features. Thus if any transaction in the stream is verified through feature 1, then it passes all the other features. Here redundancy is reduced by not checking the other two features again. 

### Feature 2

For feature 2, both the users' adjacency lists are retrieved and compared for mutual friend. If found, this passes the third feature as well and so it can be avoided.

### Feature 3
For this feature, to find if the user-2 is under 4 degree distance, I have used graph traversal to find the distance between the users. 
* Finding the length "shortest path" between the users will result in degree of seperation of the users!
* To reduce the time complexity, "Bi-directional Breadth-first search" is used. It will perform BFS from both the ends till they collide.

### Feature 4

* Intuitively, a user with abnormally huge degree of connections and who only requests funds but never pays can be suspected as a potential spammer or scammer.
* For this, the median of degrees of the all the users calculated earlier is used as base and compared with those users who in the past requested more than ever paying. If it is beyond a certain threshold (say 50%), the transaction is flagged as potentially unverified.

### Other considerations
[Back to Table of Contents] (README.md#table-of-contents)
Typically, a user who request funds more than ever paying and recieved them from many out-of fourth degree friends can be tagged as a potential spammer. This can be said as the user is never dealing with friends but rather using the app only with strangers.

## How to run? 
Run the `run.sh`. Main method is present in `PaymoAntiFraud.java` file.


## Description of Data
[Back to Table of Contents] (README.md#table-of-contents)

The `batch_payment.txt` and `stream_payment.txt` input files are formatted the same way.

As you would expect of comma-separated-value files, the first line is the header. It contains the names of all of the fields in the payment record. In this case, the fields are 

* `time`: Timestamp for the payment 
* `id1`: ID of the user making the payment 
* `id2`: ID of the user receiving the payment 
* `amount`: Amount of the payment 
* `message`: Any message the payer wants to associate with the transaction

For example, the first 10 lines (including the header) of `batch_payment.txt` or `stream_payment.txt` could look like: 

	time, id1, id2, amount, message
	2016-11-02 09:49:29, 52575, 1120, 25.32, Spam
	2016-11-02 09:49:29, 47424, 5995, 19.45, Food for 🌽 😎
	2016-11-02 09:49:29, 76352, 64866, 14.99, Clothing
	2016-11-02 09:49:29, 20449, 1552, 13.48, LoveWins
	2016-11-02 09:49:29, 28505, 45177, 19.01, 🌞🍻🌲🏔🍆
	2016-11-02 09:49:29, 56157, 16725, 4.85, 5
	2016-11-02 09:49:29, 25036, 24692, 20.42, Electric
	2016-11-02 09:49:29, 70230, 59830, 19.33, Kale Salad
	2016-11-02 09:49:29, 63967, 3197, 38.09, Diner
	 
## Original Problem Description:
https://github.com/InsightDataScience/digital-wallet
