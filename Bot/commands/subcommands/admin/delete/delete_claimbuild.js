const {capitalizeFirstLetters} = require("../../../../utils/utilities");
const {MessageEmbed} = require('discord.js');
const {serverIP, serverPort} = require("../../../../configs/config.json");
const axios = require("axios");
const {isMemberStaff} = require("../../../../utils/utilities");

module.exports = {
    async execute(interaction) {

        if (!isMemberStaff(interaction)) {
            await interaction.reply({content: "You don't have permission to use this command.", ephemeral: false});
            return;
        }

        const name = capitalizeFirstLetters(interaction.options.getString('name'));

        const data = {
            claimbuildName: name
        }

        axios.delete(`http://${serverIP}:${serverPort}/api/claimbuild/delete`, {data: data})
            .then(async function(response) {
                console.log(response.data)
                const replyEmbed = new MessageEmbed()
                    .setTitle("Staff-Deleted Claimbuild")
                    .setColor("GREEN")
                    .setDescription(`${name} has been deleted!`)
                    .addFields([
                        {name: "Unstationed Armies/Companies", value: response.data.unstationedArmies},
                        {name: "Deleted Armies/Companies", value: response.data.deletedArmies}
                    ])
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
            .catch(async function(error)  {
                console.log(error)
                const replyEmbed = new MessageEmbed()
                    .setTitle(`Error while trying to delete claimbuild: ${name}`)
                    .setColor("RED")
                    .setDescription(error.response.data.message)
                    .setTimestamp()

                await interaction.reply({embeds: [replyEmbed]})
            })
    },
};
