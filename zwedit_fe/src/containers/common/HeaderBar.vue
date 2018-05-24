<template>
    <div class="header-bar">
        <Menu mode="horizontal">
            <Row type="flex" align="middle">
                <i-col span="4">
		    <div class="header-bar-logo">
                        <Logo></Logo>
                    </div>
                </i-col>
            </Row>
        </Menu>
        <div class="clear-fix"></div>
    </div>
</template>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    .header-bar{
    }
    .header-bar-logo{
        float: left;
    }
    .header-bar-left{
    }
    .header-bar-right{
        float: right;
    }
    .clear-fix{
        clear: both;
    }
</style>

<script>
    import Logo from '@/components/common/Logo';
    
    export default {
        name: 'header-bar',
        data: () => {
            return {
            };
        },
        components: {
	    Logo,
	},
    }
</script>
