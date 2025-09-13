# bbl CLI (early scaffold)

Provides a starting point for commands like:

```
./gradlew :cli:run --args="create index webus"
```

Current status: placeholder prints stub message.

Next steps to implement real indexing:
1. Add lucene-kmp dependency (once published or via composite build) to `shared` or directly here.
2. Define canonical source layout for raw text input (e.g., `resources/raw/webus/GEN/1.txt`).
3. Implement IndexBuilder: iterate books/chapters, assign stable doc IDs, add verse content + metadata fields.
4. Write index to output directory (e.g., `build/index/webus` or pack layout `packs/webus@<version>/index`).
5. Add verification command: `bbl search webus "in the beginning"`.
6. Convert to native distributions later (jpackage / native-image / Kotlin Native after lucene-kmp K/N layer ready).
