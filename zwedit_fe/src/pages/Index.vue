<template>
    <Card>
	<div class="logo">
	   <br>
       	   <span>
              ZWEDIT: A Simple Online Share Whiteboard
           </span>
           <br>
           <br>
           <br>
	   <br>
          <Button type="primary" shape="circle" size="large" @click="createNewUrl">Create A New White Board</Button>
	  <br>
          <br>
        </div>
    </Card>
</template>
<style scoped>
    .logo {
        /*width: 180px;*/
        font-size: 18px;
        font-weight: 900;
        color: #3399ff;
        margin: 0 auto;
    }
</style>

<script>
    import request from '@/libs/request';
    export default {
        data () {
            return {
                name: 'index',
            };
	    },
        methods: {
	        createNewUrl() {
                request({
		            url: 'whiteboards',
                    method: 'post',
                }).then((res) => {
                    console.log(res);
		            window.location.href='#/whiteboard/' + res.msg;
                });
	        },
        },
    };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
