# parallel-news-aggregator
Scalable Java system with a fully parallelized processing pipeline for large-scale JSON data, using a fixed thread model and synchronization mechanisms

  - A multithreaded Java application that processes large volumes of news articles in parallel, organizing them by category and language, eliminating duplicates, extracting keyword statistics, and generating structured reports.

Features
  - Parallel processing using a fixed pool of Java Threads created once at startup
  - Duplicate elimination based on matching "uuid" or "title" fields
  - Category classification — articles sorted into category files based on a configurable list
  - Language grouping — articles grouped by language with lexicographically sorted UUIDs
  - Global article index — all articles listed chronologically (descending) with publication dates
  - Keyword analysis — word frequency across English articles, excluding linking words
  - Statistical reports — duplicates found, unique count, top author/language/category/keyword, most recent article
    
Parallelization Strategy
  - All threads are created once at startup and collaborate through the entire pipeline
  - Synchronization is handled using ConcurrentHashMap, synchronized blocks, and thread barriers to ensure deterministic, correct results
  - Article parsing: JSON files are distributed evenly among threads. Each thread processes its assigned files and stores results in a shared concurrent structure
  - Deduplication: Threads cooperate using a ConcurrentHashMap keyed by "uuid" and "title" to detect and remove duplicates
  - Classification & keyword counting**: After a barrier synchronization point, articles are split among threads for category/language assignment and English keyword extraction
  - Output writing: Final sorted results are written to disk after all threads join
  
Output Files
  - all_articles.txt = All unique articles sorted by date (desc), then UUID
  - <Category>.txt = UUIDs of articles in each category, sorted lexicographically 
  - <language>.txt = UUIDs of articles per language, sorted lexicographically 
  - keywords_count.txt = Word frequencies in English articles (desc by count, then alphabetical) 
  - reports.txt = Aggregate statistics (duplicates, top author, top language, etc.) 

Building & Running
  - Make sure to give exec permissions to the checker (chmod +x) and then run the checker to see the real-time scalability of the project
