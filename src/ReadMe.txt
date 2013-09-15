                          QryEval, version 1.0.
                                August 28, 2013


This software illustrates the architecture for the portion of a search
engine that evaluates queries.  It is a template for class homework
assignments, so it emphasizes simplicity over efficiency.  It has just
a few main components.

QryEval is the main class. Given a parameter file which specifies the 
index path in a key value pair (index=path_to_index), 
it opens the index and evaluates some hard-coded query trees and prints 
the results. You will need to modify this class so that it reads in an
external query file and evaluates queries in the file. To do this, you will 
also need to write a parser for queries. This should be fairly simple, 
given that the queries use prefix operators. Make sure to use the provided
tokenizeQuery(..) method to process the raw query terms. Otherwise, you may get
zero results for queries that include stopwords or plural words! 

QryOp is an abstract class for query operators (e.g., AND, OR, SYN,
OD/n, UW/n, etc).  Each type of query operator extends this class with
a subclass (e.g., QryopAnd).  This implementation contains 4 types of
query operators:

  * The Term operator, which just fetches an inverted list from the index;

  * The Syn operator, which combines inverted lists;

  * The Score operator, which converts an inverted list into a score list; and

  * The And operator, which combines score lists.

It is convenient to treat query operators as members of one class that
return the same type of result, but some produce inverted lists (e.g.,
Term, Syn), whereas others produce score lists (e.g., Score, And).
The solution is for all query operators to return QryResult objects
that encapsulate both types of result.  Some query operators return
populated inverted lists and empty score lists; other query operators
return empty inverted lists and populated score lists.

The ScoreList class provides a very simple implementation of a score
list.

The InvList class provides a very simple implementation of an inverted
list.

This software implements an unranked Boolean retrieval model.  It is
easily extended to other retrieval models.  For example, to implement
the Indri retrieval model, do the following.

  * Modify the QryopScore function to calculate a query likelihood
    score with Dirichlet smoothing.

  * Modify the query operators that operate on score lists (e.g., And)
    to implement the Indri score combinations.

If you want to support several retrieval models with the same software
(e.g., ranked Boolean, Okapi BM25, Indri), create different subclasses
for different models, e.g., QryOpBM25Score, QryOpIndriScore,
QryOpIndriAnd, etc.  A parameter setting or a flag in the query parser
instructs it which subclasses to use when creating query trees.
