# feedfrenzy-backend

Stateless microservice, responsible for parsing web pages and structuring the data.

This service does not require any form of authentication and/or persistence.


See [wiki](https://github.com/dekkr/feedfrenzy-backend/wiki) for more details



## Examples: 

Parse a page for article links
 
 ``` bash
 http -b POST http://localhost:8029/v1/createArticleLinks < ./src/test/testware/json/createArticleLinks-eff-deeplinks.json 
 ```

Result
  
```json
{
    "urls": [
        "https://www.eff.org/deeplinks/2015/10/final-leaked-tpp-text-all-we-feared", 
        "https://www.eff.org/deeplinks/2015/10/what-do-yoga-and-apis-have-common-neither-are-copyrightable", 
        "https://www.eff.org/deeplinks/2015/10/where-do-major-tech-companies-stand-encryption", 
        "https://www.eff.org/deeplinks/2015/10/partial-victory-obama-encryption-policy-reject-laws-mandating-backdoors-leaves", 
        "https://www.eff.org/deeplinks/2015/10/what-we-know-so-far-about-digital-rights-still-secret-final-tpp-text", 
        "https://www.eff.org/deeplinks/2015/10/victory-california-gov-brown-signs-calecpa-requiring-police-get-warrant-accessing", 
        "https://www.eff.org/deeplinks/2015/10/how-twitter-ceos-return-could-help-company-get-back-its-free-expression-roots", 
        "https://www.eff.org/deeplinks/2015/09/the-nsa-and-eff-agree"
    ]
}
```


Parse an article

``` bash
 http -b POST http://localhost:8029/v1/createArticle < ./src/test/testware/json/createArticle-eff-tpp-fights.json
 ```
 
Result
 
```json
{
  "author": "Maira Sutton", 
  "content": "Example content: October 5, 2015 | By \n<a href=\"https://www.eff.org/about/staff/maira-sutton\">Maira Sutton</a>", 
  "tags": [
      "Fair Use and Intellectual Property: Defending the Balance", 
      "International", 
      "Trade Agreements and Digital Rights", 
      "Trans-Pacific Partnership Agreement"
  ], 
  "title": "Trade Officials Announce Conclusion of TPP—Now the Real Fight Begins", 
  "uid": "https://www.eff.org/deeplinks/2015/10/trade-officials-announce-conclusion-tpp-now-real-fight-begins" 
}
```
