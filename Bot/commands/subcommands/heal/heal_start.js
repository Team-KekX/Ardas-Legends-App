
module.exports = {
    async execute(interaction) {
        let name=interaction.options.getString('army-name').toLowerCase();
        let claimbuild_name=interaction.options.getString('claimbuild-name').toLowerCase();
        const tokens = interaction.options.getInteger('tokens');
        //split the above strings into arrays of strings
        //whenever a blank space is encountered

        const arr_name = name.split(" ");
        const arr_claimbuild_name = claimbuild_name.split(" ");

        //loop through each element of the array and capitalize the first letter.

        for (let i = 0; i < arr_name.length; i++) {
            arr_name[i] = arr_name[i].charAt(0).toUpperCase() + arr_name[i].slice(1);
        }
        for (let i = 0; i < arr_claimbuild_name.length; i++) {
            arr_claimbuild_name[i] = arr_claimbuild_name[i].charAt(0).toUpperCase() + arr_claimbuild_name[i].slice(1);
        }

        //Join all the elements of the array back into a string
        //using a blankspace as a separator
        name = arr_name.join(" ");
        claimbuild_name = arr_claimbuild_name.join(" ");
        await interaction.reply(`${name} has started healing ${tokens} in ${claimbuild_name}.`);
    },
};