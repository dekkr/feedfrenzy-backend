# feedfrenzy-backend

Stateless microservice, responsible for parsing web pages and structuring the data.

This service does not require any form of authentication and/or persistence.


See [wiki](https://github.com/dekkr/feedfrenzy-backend/wiki) for more details



## Examples: 

Parse a page for article links
 
 ``` bash
 http POST http://localhost:8029/v1/createArticleLinks < ./src/test/testware/json/createArticleLinks-eff-deeplinks.json 
 ```


Parse an article

``` bash
 http POST http://localhost:8029/v1/createArticle < ./src/test/testware/json/createArticle-eff-tpp-fights.json
 ```