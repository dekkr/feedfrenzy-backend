[![License](http://img.shields.io/:license-mit-blue.svg)](http://doge.mit-license.org)
[![Build Status](https://travis-ci.org/dekkr/feedfrenzy-backend.svg?branch=master)](https://travis-ci.org/dekkr/feedfrenzy-backend)
[![Coverage Status](https://coveralls.io/repos/dekkr/feedfrenzy-backend/badge.svg?branch=master&service=github)](https://coveralls.io/github/dekkr/feedfrenzy-backend?branch=master)

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

Parse an page to get an article list

``` bash
 http -b POST http://localhost:8029/v1/createArticleList < ./src/test/testware/json/createArticleList-HN-comments.json
 ```
 
Result
 
```json
[{
    "content": "Win8 and Win10 are objectively worse than 7.\n \n",
    "tags": [],
    "title": "\"...customers are likely to see regressions with W...",
    "uid": "https://news.ycombinator.com/item?id=10918462"
}, {
    "content": "Leave the pun threads on Reddit, please.\n \n",
    "tags": [],
    "title": "Why London Underground stopped people walking up t...",
    "uid": "https://news.ycombinator.com/item?id=10916704"
}, {
    "content": "Yes, I know what a straddle is, but it has almost nothing to do with the initial claim: \"Its actual value is probably unchanged.\"\n \n",
    "tags": [],
    "title": "Etsy stock has lost 76% of its value in 9 months",
    "uid": "https://news.ycombinator.com/item?id=10912225"
}
]
```


##Docker images

Create the image (optional)

```bash
sbt docker:publishLocal
```

Start the backend

```bash
 docker run -p 8029:8029 --link pagefetch-postgres:postgres -d dekkr/feedfrenzy-backend:latest
 ```
 
 To start the whole stack so far:

 ```bash
docker run --name pagefetch-postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=pagefetcher -d postgres:latest
docker run -p 8080:8080 --name pagefetcher --link pagefetch-postgres:postgres -d dekkr/pagefetcher:latest
docker run -p 8029:8029 --link pagefetcher:pagefetcher -d dekkr/feedfrenzy-backend:latest
 ```