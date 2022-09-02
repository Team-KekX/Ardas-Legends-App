const {capitalizeFirstLetters} = require("../../../utils/utilities");
const {availableFactions} = require("../../../configs/config.json");
const {MessageEmbed} = require("discord.js");
const {UPDATE_FACTION} = require("../../../configs/embed_thumbnails.json");
const {serverIP, serverPort} = require("../../../configs/config.json");
const axios = require("axios");

module.exports = {
    async execute(interaction) {

        const faction = capitalizeFirstLetters(interaction.options.getString('faction-name').toLowerCase());

        if (!availableFactions.includes(faction)) {
            await interaction.reply({content: `${faction} is not a valid faction.`, ephemeral: false});
            await interaction.followUp({
                content: `Available factions: ${availableFactions.join(', ')}`,
                ephemeral: false
            });
        } else {
            // send to server
            const data = {
                discordId: interaction.member.id,
                factionName: faction
            }
    
            axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/faction', data)
                .then(async function (response) {
                    // The request and data is successful.
                    const replyEmbed = new MessageEmbed()
                        .setTitle(`Update faction`)
                        .setColor('GREEN')
                        .setDescription(`You were successfully registered as ${faction}.`)
                        .setThumbnail(UPDATE_FACTION)
                        .setTimestamp()
                    await interaction.editReply({embeds: [replyEmbed], ephemeral: false});
                })
                .catch(async function (error) {
                    // An error occurred during the request.
                    await interaction.editReply({content: `${error.response.data.message}`, ephemeral: false});
                })
        }
    },
};