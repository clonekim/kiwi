# kiwi

kiwi는 wiki의 다른 이름이다.

h2db
clojure


```sql
create table kiwis (
 id identity primary key not null,
 topic varchar(200) not null,
 body varchar not null,
 create_at timestamp default current_timestamp,
 updated_at timestamp default current_timestamp,
 is_draft boolean default true
)
```
