const {capitalizeFirstLetters, isMemberStaff} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const {UPDATE} = require('../../../../configs/embed_thumbnails.json');
const axios = require("axios");

module.exports = {
    async execute(interaction) {
        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }
        //name won't get capitalized here so people have more freedom when naming their chars
        const gear = capitalizeFirstLetters(interaction.options.getString('new-gear'));

        //data sent to server
        const data = {
            discordId: interaction.options.getString('discord-id'),
            gear: gear
        }

        axios.patch('http://'+serverIP+':'+serverPort+'/api/player/update/rpchar/gear', data)
            .then(async function() {
                //if request successful
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Update RpChar Gear`)
                    .setColor('GREEN')
                    .setDescription(`The gear of your Roleplay Character has been updated to ${gear}!`)
                    .setThumbnail(UPDATE)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]});
            })
            .catch(async function(error) {
                const replyEmbed = new MessageEmbed()
                    .setTitle("Error while updating roleplay character gear")
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()
                await interaction.editReply({embeds: [replyEmbed]})
            })

    },
};