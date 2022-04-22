
### TODO

- [ ] Add device actions collection for dosing nutrients (websockets? queue? RSocket?)
- [ ] Add chart collection
- [ ] Update hydro-dose to upload readings to elevated (HTTP request? RSocket?)
- [ ] Migrate existing data
- [ ] Update Android app to point at elevated

### SWAG Configuration

`data/config/nginx/site-confs/default`

```text
error_page 502 /502.html;

# redirect all traffic to https
server {
    listen 80 default_server;
    listen [::]:80 default_server;
    server_name _;
    return 301 https://$host$request_uri;
}

server {
    listen 443 ssl http2 default_server;
    listen [::]:443 ssl http2 default_server;

    server_name _;

    # all ssl related config moved to ssl.conf
    include /config/nginx/ssl.conf;

    client_max_body_size 0;

    location / {
        include /config/nginx/proxy.conf;
        include /config/nginx/resolver.conf;
        set $upstream_app service;
        set $upstream_port 8080;
        set $upstream_proto http;
        proxy_pass $upstream_proto://$upstream_app:$upstream_port;
    }
}
```
