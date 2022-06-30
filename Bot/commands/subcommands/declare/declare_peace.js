const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {PEACE} = require('../../../configs/embed_thumbnails.json');

module.exports = {
    async execute(interaction) {
        const attacker = capitalizeFirstLetters(interaction.options.getString('attacker-faction').toLowerCase());
        const defender = capitalizeFirstLetters(interaction.options.getString('defender-faction').toLowerCase());
        const guild = interaction.client.guilds.cache.get("926452447243825192");
        const attacker_role = guild.roles.cache.find(role => role.name === `${attacker}`);
        const defender_role = guild.roles.cache.find(role => role.name === `${defender}`);
        interaction.reply({
            allowedMentions: {roles: [attacker_role.id, defender_role.id]},
            content: `<@&${attacker_role.id}> made peace with <@&${defender_role.id}>.`,
            attachments: [PEACE]
        })
    },
};