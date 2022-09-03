// When the client is ready, run this code (only once)
module.exports=
{
    name: 'ready',
    once: true,
    execute(client) {
        const log = require("log4js").getLogger();
        log.info(`Bot ready! Logged in as ${client.user.tag}`)
    },
};