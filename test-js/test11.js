var axios = require('axios');
var data = JSON.stringify({
   "username": "admin_chuanweixuan",
   "password": "jj123456"
});

var config = {
   method: 'post',
   url: 'http://127.0.0.1:8080/merchant/auth/login',
   headers: { 
      'Content-Type': 'application/json', 
      'Authorization': 'Bearer {{bearerToken}}'
   },
   data : data
};

axios(config)
.then(function (response) {
   console.log(JSON.stringify(response.data));
})
.catch(function (error) {
   console.log(error);
});
