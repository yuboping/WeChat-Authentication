requirejs.config({
    baseUrl: '/js',
    paths: {
        wx: './wx',
        util:'./util',
        jquery:'./thirdparty/jquery-1.7.2.min'
    }
});

define("common", function(){});
