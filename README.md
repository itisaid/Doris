# Doris #
## A large scale distributed KV storage system from Alibaba. ##

- Release date: 2013/5/21
- Doris is one of Alibaba technology products

## Doris's Features ##
- Scalable like HBase. Support large scale data storage and visit with scalable server deployment.
- High available than HBase. Neither lost data nor reject app's access when any server is crashed.
- High performance. Less 4 ms for commonly data access.
- Easy operation. Auto failover and data recover when server is crashed. Easy add servers to Doris cluster with web interface.
- Optimized consistence hash algorithm. More balance on distributed data than traditional consistence hash algorithm.

## Doris architecture

### High Performance
   Doris provides high performance KV access with low latency and high throughput.    

### Scalability
   Doris is designed to supported large scale cluster up to 2000+ machine node.
<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris1.jpg" width="70%" height="70%"/>

## Extensibility
   Doris provides a set of uniform KV API and extensible architecture. You can implement specific adapter to construct a cluster for some KV database. 
 
<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris2.jpg" width="70%" height="70%"/>

  Now Doris has provieded adapters for the most common KV databases by default , such as 
 - KV cache, such as Redis, Memcached etc. 
 - KV storage, such as Berkley DB, Mysql KV, KyotoCabinet etc.
 
 

# CAP of Doris
### 1  High Availability
<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris3.jpg" width="70%" height="70%"/>


<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris4.jpg" width="70%" height="70%"/>

### 2 Consistency
<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris5.jpg" width="60%" height="60%"/>

<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris6.jpg" width="60%" height="60%"/>

### 3 Failover and fault tolerant
<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris7.jpg" width="60%" height="60%"/>

<img src="https://raw.githubusercontent.com/wiki/itisaid/Doris/images/doris8.jpeg" width="75%" height="75%"/>


