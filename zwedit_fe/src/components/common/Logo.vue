<template>
    <div class="logo">
        <span>
	    <i-button type="text">
                <Icon type="clipboard" size="24"></Icon>
            </i-button>
            ZWEDIT
        </span>
    </div>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
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
    export default {
        name: 'logo',
        data: () => {
            return {};
        },
    };
</script>
